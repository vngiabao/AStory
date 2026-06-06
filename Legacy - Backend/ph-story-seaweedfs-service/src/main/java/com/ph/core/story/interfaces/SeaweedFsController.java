package com.ph.core.story.interfaces;

import com.ph.core.story.application.seaweedfs.SeaweedFsObjectService;
import com.ph.core.story.application.seaweedfs.dto.FileMetaResponse;
import com.ph.core.story.application.seaweedfs.dto.FileUploadResponse;
import com.ph.core.story.application.seaweedfs.dto.ListObjectsResponse;
import com.ph.core.story.application.seaweedfs.dto.PresignPutRequest;
import com.ph.core.story.application.seaweedfs.dto.PresignUrlResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class SeaweedFsController {

    private final SeaweedFsObjectService service;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        service.assertBucketAccessible();
        return ResponseEntity.ok("SeaweedFS OK");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse upload(@RequestPart("file") MultipartFile file,
            @RequestParam(value = "prefix", required = false) String prefix) {
        return service.uploadAuto(file, prefix);
    }

    // MultipartFile upload có thể dùng cho các form upload truyền thống, còn presign URL sẽ phù hợp
    // với các client hiện đại (SPA, mobile app) muốn upload trực tiếp đến S3 mà không qua backend.
    // Cho phép upload nhiều file cùng lúc bằng cách nhận List<MultipartFile> thay vì MultipartFile
    // đơn lẻ, và trả về List<FileUploadResponse>.
    @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<FileUploadResponse> uploads(@RequestPart("files") List<MultipartFile> files,
            @RequestParam(value = "prefix", required = false) String prefix) {
        return service.uploads(files, prefix);
    }

    @GetMapping(value = "/download")
    public void download(@RequestParam("key") String key,
            @RequestParam(name = "download", defaultValue = "false") boolean download,
            @RequestParam(name = "cacheSeconds", defaultValue = "0") long cacheSeconds,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (ResponseInputStream<GetObjectResponse> in = service.download(key)) {
            GetObjectResponse meta = in.response();
            String contentType = meta.contentType() != null ? meta.contentType()
                    : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            response.setContentType(contentType);
            if (meta.contentLength() != null) {
                response.setHeader(HttpHeaders.CONTENT_LENGTH,
                        String.valueOf(meta.contentLength()));
            }

            if (meta.eTag() != null) {
                // Set ETag header để client có thể cache và validate lại sau này.
                response.setHeader(HttpHeaders.ETAG, meta.eTag());
                // Kiểm tra If-None-Match header từ client để hỗ trợ cache hiệu quả.
                String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
                if (ifNoneMatch != null && ifNoneMatch.equals(meta.eTag())) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }

            if (cacheSeconds > 0) {
                // "private" vì các endpoint này thường có auth phía trước.
                // nhằm tránh cache ở proxy trung gian (CDN, gateway) mà vẫn cho phép browser cache.
                response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + cacheSeconds);
            }

            // Sử dụng key làm fallback cho filename (client có thể override).
            // Nếu key có dạng "folder/subfolder/file.txt" thì filename sẽ là "file.txt".
            String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
            // attachment -> trình duyệt sẽ tải file về, inline -> trình duyệt sẽ cố gắng mở file
            // nếu hỗ trợ.
            String dispositionType = download ? "attachment" : "inline";
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, dispositionType
                    + "; filename*=UTF-8''" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            // Stream file content trực tiếp từ S3 đến response output stream mà không cần load toàn
            // bộ vào memory.
            in.transferTo(response.getOutputStream());
        }
    }

    @PostMapping("/copy")
    public void copy(@RequestParam String fromKey, @RequestParam String toKey) {
        service.copy(fromKey, toKey);
    }

    @PostMapping("/rename")
    public void rename(@RequestParam String fromKey, @RequestParam String toKey) {
        service.rename(fromKey, toKey);
    }

    @GetMapping("/meta")
    public FileMetaResponse meta(@RequestParam("key") String key) {
        return service.head(key);
    }

    @DeleteMapping
    public void delete(@RequestParam("key") String key) {
        service.delete(key);
    }

    @GetMapping("/presign/get")
    public PresignUrlResponse presignGet(@RequestParam("key") String key,
            @RequestParam(value = "expiresInSeconds", required = false) Long expiresInSeconds) {
        return service.presignGet(key, expiresInSeconds);
    }

    @PostMapping("/presign/put")
    public PresignUrlResponse presignPut(@Valid @RequestBody PresignPutRequest request) {
        return service.presignPut(request);
    }

    @GetMapping("/list")
    public ListObjectsResponse list(@RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "continuationToken", required = false) String continuationToken,
            @RequestParam(value = "maxKeys", required = false) Integer maxKeys) {
        return service.list(prefix, continuationToken, maxKeys);
    }
}

