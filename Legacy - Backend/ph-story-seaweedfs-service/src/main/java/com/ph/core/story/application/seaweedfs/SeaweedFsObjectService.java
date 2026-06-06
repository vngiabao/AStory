package com.ph.core.story.application.seaweedfs;

import com.ph.core.story.application.seaweedfs.dto.FileMetaResponse;
import com.ph.core.story.application.seaweedfs.dto.FileUploadResponse;
import com.ph.core.story.application.seaweedfs.dto.ListObjectsResponse;
import com.ph.core.story.application.seaweedfs.dto.PresignPutRequest;
import com.ph.core.story.application.seaweedfs.dto.PresignUrlResponse;
import com.ph.core.story.common.config.SeaweedFsS3Properties;
import com.ph.core.story.common.exception.BaseBusinessException;
import com.ph.core.story.common.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.MetadataDirective;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeaweedFsObjectService {

    private static final DateTimeFormatter ISO_INSTANT = DateTimeFormatter.ISO_INSTANT;

    private final SeaweedFsS3Properties props;
    private final S3Client s3;
    private final S3Presigner presigner;

    @PostConstruct
    public void init() {
        ensureBucketExists();
    }

    public void ensureBucketExists() {
        String bucket = props.getBucketName();

        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            // Bucket chưa tồn tại → tạo
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception e) {
            // Một số trường hợp SeaweedFS không throw NoSuchBucketException
            if (e.statusCode() == 404 || "NoSuchBucket".equals(e.awsErrorDetails().errorCode())) {
                s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            } else {
                throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                        "Failed to check/create bucket",
                        Map.of("bucket", bucket, "awsErrorCode",
                                e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode()
                                        : null));
            }
        }
    }

    public void assertBucketAccessible() {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(props.getBucketName()).build());
        } catch (S3Exception ex) {
            throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "SeaweedFS bucket is not accessible",
                    Map.of("bucket", props.getBucketName(), "awsErrorCode",
                            ex.awsErrorDetails() != null ? ex.awsErrorDetails().errorCode()
                                    : null));
        }
    }

    // ==========================
    // PUBLIC API - UPLOAD FILES
    // Upload nhiều file cùng lúc để phục vụ các form upload truyền thống hoặc các client muốn
    // upload nhiều file trong 1 request.
    // ==========================
    public List<FileUploadResponse> uploads(List<MultipartFile> files, String prefix) {
        if (files == null || files.isEmpty()) {
            throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR,
                    "At least one file is required");
        }

        // Dùng stream để xử lý từng file, gọi uploadAuto cho mỗi file
        return files.stream().map(file -> uploadAuto(file, prefix)).collect(Collectors.toList());
    }

    // ==========================
    // PUBLIC API (ENTRY POINT)
    // ==========================
    public FileUploadResponse uploadAuto(MultipartFile file, String prefix) {
        validateFile(file);

        // FIX QUAN TRỌNG: dùng file.getSize() thay vì file.getBytes().length
        // để tránh đọc toàn bộ file vào bộ nhớ
        // Nếu file lớn hơn threshold thì dùng multipart upload,
        // ngược lại dùng putObject thông thường
        // Threshold có thể cấu hình qua props, mặc định 20MB
        long threshold = props.getMaxFileSizeMb() * 1024L * 1024L;

        if (file.getSize() > threshold) {
            // Dùng multipart upload cho file lớn hơn threshold
            return uploadLarge(file, prefix);
        } else {
            // Dùng putObject thông thường cho file nhỏ hơn hoặc bằng threshold
            return uploadSimple(file, prefix);
        }
    }

    private FileUploadResponse uploadSimple(MultipartFile file, String prefix) {
        String originalName = getSafeFileName(file);
        // FIX QUAN TRỌNG: buildObjectKey phải dùng originalName
        // đã được làm sạch để tránh lỗi ký tự đặc biệt
        String key = buildObjectKey(prefix, originalName);

        // FIX QUAN TRỌNG: xác định contentType trước khi upload
        // Nếu MultipartFile không cung cấp contentType, mặc định là application/octet-stream
        String contentType = StringUtils.hasText(file.getContentType()) ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        // FIX QUAN TRỌNG: thêm metadata để lưu tên gốc và thời gian upload
        Map<String, String> meta = buildMetadata(originalName);

        // FIX QUAN TRỌNG: luôn set ACL là private để tránh bị public mặc định
        PutObjectRequest req = PutObjectRequest.builder()
                // Bucket phải lấy từ props, không hardcode để tránh lỗi khi đổi cấu hình
                .bucket(props.getBucketName())
                // Key phải dùng key đã được build, không dùng originalName để tránh lỗi trùng tên
                // và ký tự đặc biệt
                .key(key)
                // ContentType phải set để SeaweedFS lưu đúng loại file, nếu không sẽ bị
                // application/octet-stream mặc định
                .contentType(contentType)
                // Metadata phải set để lưu thông tin bổ sung về file, nếu không sẽ mất thông tin
                // gốc như tên gốc và thời gian upload
                .metadata(meta)
                // ACL nên set là private để tránh bị public mặc định, nếu không sẽ có nguy cơ bị
                // truy cập trái phép nếu bucket có cấu hình public
                .acl(ObjectCannedACL.PRIVATE)
                // build request
                .build();

        try {
            // FIX QUAN TRỌNG: dùng bytes để tránh upload 0B
            byte[] bytes = file.getBytes();

            if (bytes.length == 0) {
                throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR,
                        "File content is empty");
            }

            // phải dùng req đã build ở trên để đảm bảo tất cả thông tin được gửi đi, không được tạo
            // PutObjectRequest mới
            // vì nếu tạo mới sẽ mất hết thông tin đã set như contentType, metadata, ACL
            var resp = s3.putObject(req, RequestBody.fromBytes(bytes));

            // build response phải dùng thông tin từ req và resp, không được dùng
            // file.getContentType()
            // hoặc file.getSize() trực tiếp vì có thể bị khác do xử lý của S3
            return buildResponse(key, contentType, file.getSize(), resp.eTag());

        } catch (IOException ex) {
            throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Failed to read file upload stream");
        } catch (S3Exception ex) {
            throw toBusiness(ex, "Upload failed", Map.of("key", key));
        }
    }

    private FileUploadResponse uploadLarge(MultipartFile file, String prefix) {
        String originalName = getSafeFileName(file);
        String key = buildObjectKey(prefix, originalName);
        String bucket = props.getBucketName();

        String contentType = StringUtils.hasText(file.getContentType()) ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        Map<String, String> meta = buildMetadata(originalName);

        String uploadId = null;

        try {
            // ==========================
            // 1. CREATE MULTIPART
            // ==========================
            CreateMultipartUploadResponse createResp =
                    s3.createMultipartUpload(CreateMultipartUploadRequest.builder().bucket(bucket)
                            .key(key).contentType(contentType) // FIX: thêm contentType
                            .metadata(meta) // FIX: thêm metadata
                            .acl(ObjectCannedACL.PRIVATE).build());

            uploadId = createResp.uploadId();

            List<CompletedPart> completedParts = new ArrayList<>();

            int partSize = 5 * 1024 * 1024; // 5MB
            byte[] buffer = new byte[partSize];

            // ==========================
            // 2. UPLOAD PARTS
            // ==========================
            try (InputStream is = file.getInputStream()) {
                int partNumber = 1;
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {

                    UploadPartResponse uploadPartResponse = s3.uploadPart(
                            UploadPartRequest.builder().bucket(bucket).key(key).uploadId(uploadId)
                                    .partNumber(partNumber).contentLength((long) bytesRead).build(),
                            RequestBody.fromBytes(Arrays.copyOf(buffer, bytesRead)));

                    completedParts.add(CompletedPart.builder().partNumber(partNumber)
                            .eTag(uploadPartResponse.eTag()).build());

                    partNumber++;
                }
            }

            // ==========================
            // 3. COMPLETE UPLOAD
            // ==========================
            s3.completeMultipartUpload(CompleteMultipartUploadRequest.builder().bucket(bucket)
                    .key(key).uploadId(uploadId)
                    .multipartUpload(
                            CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build());

            return buildResponse(key, contentType, file.getSize(), null);

        } catch (Exception ex) {

            // ==========================
            // FIX QUAN TRỌNG: ABORT nếu fail
            // ==========================
            if (uploadId != null) {
                try {
                    s3.abortMultipartUpload(AbortMultipartUploadRequest.builder().bucket(bucket)
                            .key(key).uploadId(uploadId).build());
                } catch (Exception abortEx) {
                    // log thôi, không throw thêm
                }
            }

            throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Multipart upload failed", Map.of("error", ex.getMessage()));
        }
    }

    // ==========================
    // VALIDATION
    // ==========================
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR, "File is required");
        }

        long maxBytes = props.getMaxFileSizeMb() * 1024L * 1024L;

        if (file.getSize() > maxBytes) {
            throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR, "File size exceeds limit",
                    Map.of("maxFileSizeMb", props.getMaxFileSizeMb(), "size", file.getSize()));
        }

        String originalName = getSafeFileName(file);
        String ext = getExtension(originalName);

        if (!isAllowedExtension(ext)) {
            throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR,
                    "File extension is not allowed",
                    Map.of("extension", ext, "allowedExtensions", props.getAllowedExtensions()));
        }
    }

    // ==========================
    // SAFE FILENAME
    // ==========================
    private String getSafeFileName(MultipartFile file) {
        return StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename()
                : "file";
    }

    // ==========================
    // METADATA BUILDER
    // ==========================
    private Map<String, String> buildMetadata(String originalName) {
        Map<String, String> meta = new HashMap<>();
        meta.put("original-filename", originalName);
        meta.put("uploaded-at", Instant.now().toString());
        return meta;
    }

    // ==========================
    // RESPONSE BUILDER
    // ==========================
    private FileUploadResponse buildResponse(String key, String contentType, long size,
            String eTag) {
        return FileUploadResponse.builder().bucket(props.getBucketName()).key(key).eTag(eTag)
                .size(size).contentType(contentType).build();
    }

    public ResponseInputStream<GetObjectResponse> download(String key) {
        validateKey(key);
        try {
            return s3.getObject(
                    GetObjectRequest.builder().bucket(props.getBucketName()).key(key).build());
        } catch (NoSuchKeyException ex) {
            throw new BaseBusinessException(ErrorCode.NOT_FOUND, "Object not found",
                    Map.of("key", key));
        } catch (S3Exception ex) {
            throw toBusiness(ex, "Download failed", Map.of("key", key));
        }
    }

    public FileMetaResponse head(String key) {
        validateKey(key);
        try {
            var head = s3.headObject(
                    HeadObjectRequest.builder().bucket(props.getBucketName()).key(key).build());
            return FileMetaResponse.builder().bucket(props.getBucketName()).key(key)
                    .eTag(head.eTag()).contentLength(head.contentLength())
                    .contentType(head.contentType()).lastModified(head.lastModified())
                    .metadata(head.metadata()).build();
        } catch (NoSuchKeyException ex) {
            throw new BaseBusinessException(ErrorCode.NOT_FOUND, "Object not found",
                    Map.of("key", key));
        } catch (S3Exception ex) {
            throw toBusiness(ex, "Fetch metadata failed", Map.of("key", key));
        }
    }

    public void delete(String key) {
        validateKey(key);
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                    // Bucket name
                    .bucket(props.getBucketName())
                    // Object key
                    .key(key)
                    // Build request
                    .build());
        } catch (S3Exception ex) {
            throw toBusiness(ex, "Delete failed", Map.of("key", key));
        }
    }

    public void copy(String fromKey, String toKey) {
        // Validate input key
        validateKey(fromKey);
        validateKey(toKey);

        // Nếu source và destination giống nhau thì không cần copy
        if (fromKey.equals(toKey)) {
            return;
        }

        // Lấy bucket từ config
        String bucket = props.getBucketName();

        try {
            s3.copyObject(CopyObjectRequest.builder()

                    // =========================
                    // DESTINATION (đích)
                    // =========================

                    // Bucket đích: nơi object sẽ được lưu sau khi copy
                    .destinationBucket(bucket)

                    // Key đích: đường dẫn + tên file mới sau khi copy
                    .destinationKey(toKey)

                    // =========================
                    // SOURCE (nguồn)
                    // =========================

                    // Bucket nguồn: nơi chứa object gốc
                    .sourceBucket(bucket)

                    // Key nguồn: key của object cần copy
                    // CHỈ là key (vd: folder/file.png), KHÔNG phải "bucket/key"
                    .sourceKey(fromKey)

                    // =========================
                    // OPTIONS
                    // =========================

                    // Giữ nguyên metadata của object gốc (content-type, custom metadata, ...)
                    .metadataDirective(MetadataDirective.COPY)

                    // Set quyền private để tránh bị public ngoài ý muốn
                    .acl(ObjectCannedACL.PRIVATE)

                    // Build request
                    .build());

        } catch (S3Exception ex) {
            // Convert exception từ S3 sang business exception của hệ thống
            throw toBusiness(ex, "Copy failed", Map.of("fromKey", fromKey, "toKey", toKey));
        }
    }

    public void rename(String fromKey, String toKey) {

        // Validate key đầu vào (không null, không chứa ký tự nguy hiểm, ...)
        validateKey(fromKey);
        validateKey(toKey);

        // Nếu key nguồn và đích giống nhau thì không cần xử lý
        if (fromKey.equals(toKey)) {
            return;
        }

        // =========================
        // STEP 1: COPY
        // =========================
        // Copy object từ fromKey → toKey trong cùng bucket
        // Đây là cách duy nhất để "rename" trong S3-compatible storage
        // vì S3 không hỗ trợ rename trực tiếp
        copy(fromKey, toKey);

        // =========================
        // STEP 2: DELETE
        // =========================
        // Sau khi copy thành công, xóa object cũ
        // --> hoàn tất quá trình rename
        delete(fromKey);
    }

    public PresignUrlResponse presignGet(String key, Long expiresInSeconds) {

        // =========================
        // STEP 0: Validate input
        // =========================
        // Kiểm tra key hợp lệ (không null, không chứa "..", không bắt đầu bằng "/")
        validateKey(key);

        // =========================
        // STEP 1: Xác định thời gian hết hạn (expiry)
        // =========================
        // Nếu client truyền expiresInSeconds thì dùng,
        // nếu không thì fallback về config mặc định trong properties
        long exp = expiresInSeconds != null ? expiresInSeconds
                : props.getPresignDefaultExpirySeconds();

        // Convert sang Duration để dùng cho presigner
        Duration expiry = Duration.ofSeconds(exp);

        // =========================
        // STEP 2: Build GetObjectRequest
        // =========================
        // Đây là request gốc (giống như khi gọi getObject bình thường)
        // nhưng sẽ được "ký" để tạo URL tạm thời
        // bucket chứa file
        GetObjectRequest getReq = GetObjectRequest.builder()
                // Bucket name
                .bucket(props.getBucketName())
                // object key (đường dẫn file)
                .key(key)
                // Build request
                .build();

        // =========================
        // STEP 3: Generate presigned URL
        // =========================
        // presigner sẽ:
        // - ký request bằng accessKey + secretKey
        // - tạo URL có chứa chữ ký (signature)
        // - URL này có thể dùng trực tiếp (không cần auth nữa)
        PresignedGetObjectRequest presigned =
                presigner.presignGetObject(GetObjectPresignRequest.builder()
                        // thời gian hết hạn của URL
                        .signatureDuration(expiry)
                        // request gốc cần ký
                        .getObjectRequest(getReq).build());

        // =========================
        // STEP 4: Return response cho client
        // =========================
        return PresignUrlResponse.builder()
                // HTTP method cần dùng khi gọi URL này
                .method("GET")
                // URL đã được ký → client có thể dùng trực tiếp để download file
                .url(presigned.url().toString())
                // thời gian hết hạn (seconds)
                .expiresInSeconds(exp).build();
    }

    public PresignUrlResponse presignPut(PresignPutRequest req) {

        // =========================
        // STEP 0: Validate input
        // =========================
        // Kiểm tra key hợp lệ (tránh path traversal, key rỗng, ...)
        validateKey(req.getKey());

        // =========================
        // STEP 1: Xác định thời gian hết hạn
        // =========================
        // Nếu client truyền expiresInSeconds → dùng
        // Nếu không → fallback config mặc định
        long exp = req.getExpiresInSeconds() != null ? req.getExpiresInSeconds()
                : props.getPresignDefaultExpirySeconds();

        Duration expiry = Duration.ofSeconds(exp);

        // =========================
        // STEP 2: Xác định Content-Type
        // =========================
        // Content-Type rất quan trọng:
        // - phải giống lúc client upload
        // - nếu lệch → có thể bị reject (signature mismatch)
        String contentType = StringUtils.hasText(req.getContentType()) ? req.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        // =========================
        // STEP 3: Build PutObjectRequest (request gốc)
        // =========================
        // Đây là request upload thật sự, sẽ được ký lại thành presigned URL
        PutObjectRequest putReq = PutObjectRequest.builder()
                // bucket đích
                .bucket(props.getBucketName())
                // key (đường dẫn file)
                .key(req.getKey())
                // phải match với client upload
                .contentType(contentType)
                // set private để tránh public file
                .acl(ObjectCannedACL.PRIVATE)
                // build request gốc
                .build();

        // =========================
        // STEP 4: Generate presigned URL
        // =========================
        // presigner sẽ:
        // - ký request bằng credentials
        // - generate URL tạm thời
        // - client dùng URL này để upload trực tiếp lên S3/SeaweedFS
        PresignedPutObjectRequest presigned =
                presigner.presignPutObject(PutObjectPresignRequest.builder()
                        // thời gian hiệu lực
                        .signatureDuration(expiry)
                        // request cần ký
                        .putObjectRequest(putReq)
                        // build presign request
                        .build());

        // =========================
        // STEP 5: Trả về cho client
        // =========================
        return PresignUrlResponse.builder()
                // method phải dùng khi upload
                .method("PUT")
                // URL đã ký
                .url(presigned.url().toString())
                // thời gian hết hạn (seconds)
                .expiresInSeconds(exp)
                // Build response object chứa URL và thông tin liên quan
                .build();
    }

    public ListObjectsResponse list(String prefix, String continuationToken, Integer maxKeys) {

        // =========================
        // STEP 0: Chuẩn hóa input
        // =========================

        // prefix:
        // - dùng để filter object theo "folder ảo"
        // - ví dụ: "story-assets/2026/03/"
        // - nếu null → lấy toàn bộ bucket
        String p = prefix != null ? prefix : "";

        // maxKeys:
        // - số lượng object trả về trong 1 lần gọi
        // - S3 giới hạn tối đa 1000
        // - đảm bảo nằm trong [1, 1000]
        int mk = maxKeys != null ? Math.min(Math.max(maxKeys, 1), 1000) : 100; // default = 100

        try {

            // =========================
            // STEP 1: Gọi S3 listObjectsV2
            // =========================
            var resp = s3.listObjectsV2(ListObjectsV2Request.builder()
                    // bucket cần list
                    .bucket(props.getBucketName())
                    // filter theo prefix
                    .prefix(p)
                    // token để phân trang
                    .continuationToken(continuationToken)
                    // số lượng item mỗi page
                    .maxKeys(mk)
                    // build request
                    .build());

            // =========================
            // STEP 2: Mapping response
            // =========================
            return ListObjectsResponse.builder().bucket(props.getBucketName()) // bucket

                    // prefix đang query (giúp client debug / hiển thị)
                    .prefix(p)

                    // truncated = true -> còn dữ liệu phía sau (chưa lấy hết)
                    .truncated(resp.isTruncated())

                    // token cho page tiếp theo (nếu truncated = true)
                    .nextContinuationToken(resp.nextContinuationToken())

                    // danh sách object (map từ S3Object → DTO của bạn)
                    .items(resp.contents().stream().map(this::mapItem).collect(Collectors.toList()))

                    .build();

        } catch (S3Exception ex) {

            // =========================
            // STEP 3: Handle error
            // =========================
            throw toBusiness(ex, "List objects failed", Map.of("prefix", p));
        }
    }

    // private ListObjectsResponse.ObjectItem mapItem(S3Object o) {
    // return ListObjectsResponse.ObjectItem.builder().key(o.key()).size(o.size()).eTag(o.eTag())
    // .lastModified(
    // o.lastModified() != null ? ISO_INSTANT.format(o.lastModified()) : null)
    // .build();
    // }

    private ListObjectsResponse.ObjectItem mapItem(S3Object o) {

        // =========================
        // STEP 1: Defensive null check
        // =========================
        if (o == null) {
            return null;
        }

        // =========================
        // STEP 2: Normalize dữ liệu
        // =========================

        String key = o.key();

        // size có thể null trong một số case hiếm (defensive)
        Long size = o.size();

        // eTag thường có dạng "abc123" -> nên strip dấu "
        String eTag = o.eTag();
        if (eTag != null && eTag.length() >= 2 && eTag.startsWith("\"") && eTag.endsWith("\"")) {
            eTag = eTag.substring(1, eTag.length() - 1);
        }

        // lastModified → format ISO (UTC)
        String lastModified =
                o.lastModified() != null ? ISO_INSTANT.format(o.lastModified()) : null;

        // =========================
        // STEP 3: Derive thêm thông tin hữu ích cho FE
        // =========================

        // filename (phục vụ UI hiển thị)
        String fileName = null;
        if (key != null) {
            int idx = key.lastIndexOf('/');
            fileName = (idx >= 0) ? key.substring(idx + 1) : key;
        }

        // extension
        String extension = null;
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }

        return ListObjectsResponse.ObjectItem.builder()
                // full path của object trong bucket (có thể chứa "folder ảo")
                .key(key)
                // tên file (UI friendly)
                .fileName(fileName)
                // phần mở rộng (extension) của file, đã normalize về lowercase
                .extension(extension)
                // kích thước file (có thể null nếu S3 không trả về)
                .size(size)
                // eTag đã được strip dấu " nếu có
                .eTag(eTag)
                // thời gian last modified đã format ISO (UTC)
                .lastModified(lastModified)
                // build DTO trả về cho client
                .build();
    }

    private void validateKey(String key) {
        if (!StringUtils.hasText(key)) {
            throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR, "Key is required");
        }
        if (key.startsWith("/") || key.contains("..")) {
            throw new BaseBusinessException(ErrorCode.VALIDATION_ERROR, "Invalid key",
                    Map.of("key", key));
        }
    }

    private boolean isAllowedExtension(String ext) {
        if (!StringUtils.hasText(ext))
            return false;
        String normalized = ext.toLowerCase(Locale.ROOT);
        return props.getAllowedExtensions().stream().map(s -> s.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::equals);
    }

    private String getExtension(String filename) {
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1)
            return "";
        return filename.substring(idx + 1);
    }

    private String buildObjectKey(String prefix, String originalName) {
        String cleanPrefix = StringUtils.hasText(prefix) ? prefix.trim() : "";
        cleanPrefix = cleanPrefix.replace("\\", "/");
        while (cleanPrefix.startsWith("/"))
            cleanPrefix = cleanPrefix.substring(1);
        while (cleanPrefix.endsWith("/"))
            cleanPrefix = cleanPrefix.substring(0, cleanPrefix.length() - 1);

        String cleanName = originalName.replace("\\", "_").replace("/", "_");

        LocalDate d = LocalDate.now(ZoneOffset.UTC);
        String datePath = d.getYear() + "/" + String.format("%02d", d.getMonthValue()) + "/"
                + String.format("%02d", d.getDayOfMonth());

        String uuid = UUID.randomUUID().toString();
        String base = (StringUtils.hasText(cleanPrefix) ? cleanPrefix + "/" : "") + datePath + "/"
                + uuid + "_" + cleanName;
        return base;
    }

    private BaseBusinessException toBusiness(S3Exception ex, String message,
            Map<String, Object> details) {
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        int status = ex.statusCode();
        if (status == 404)
            code = ErrorCode.NOT_FOUND;
        else if (status == 400)
            code = ErrorCode.BAD_REQUEST;
        else if (status == 401)
            code = ErrorCode.UNAUTHORIZED;
        else if (status == 403)
            code = ErrorCode.FORBIDDEN;

        Map<String, Object> merged = new HashMap<>(details != null ? details : Map.of());
        merged.put("statusCode", status);
        if (ex.awsErrorDetails() != null) {
            merged.put("awsErrorCode", ex.awsErrorDetails().errorCode());
            merged.put("awsErrorMessage", ex.awsErrorDetails().errorMessage());
        }
        return new BaseBusinessException(code, message, merged);
    }
}

