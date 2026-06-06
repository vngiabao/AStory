# ph-story-seaweedfs-service

Service Spring Boot cung cấp API thao tác file trên **SeaweedFS (S3 Gateway)** theo các endpoint chuẩn: upload/download/meta/delete/list/presign.

## Yêu cầu
- Java 17
- Maven 3.8+
- SeaweedFS chạy **S3 Gateway** (endpoint dạng `http://host:port`)

## Cấu hình (Dev)
Project đang bật profile `dev` theo `src/main/resources/application.yml`.

Các cấu hình SeaweedFS S3 dùng prefix `seaweedfs.s3`:

```yaml
seaweedfs:
  s3:
    endpoint: http://localhost:8333
    access-key: any
    secret-key: any
    region: us-east-1
    bucket-name: ph-story-bucket
    max-file-size-mb: 10
    allowed-extensions:
      - jpg
      - png
      - mp4
      - pdf
```

File cấu hình dev bạn đang để ở thư mục gốc: `ph-story-seaweedfs-service-dev.yml`.

### Cách “nạp” file `ph-story-seaweedfs-service-dev.yml` khi chạy local
Có 2 cách phổ biến:

- **Cách A (khuyến nghị cho local)**: chạy app và trỏ thêm location:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.additional-location=file:../ph-story-seaweedfs-service-dev.yml"
```

- **Cách B**: copy nội dung `seaweedfs.s3.*` vào config server (nếu môi trường dùng Spring Cloud Config).

## Chạy service
```bash
cd ph-story-seaweedfs-service
mvn spring-boot:run
```

Mặc định dev config đang để `server.port: 9106` (trong `ph-story-seaweedfs-service-dev.yml`).

## Authentication (quan trọng khi test Postman)
Security config hiện tại yêu cầu **JWT Bearer token** cho hầu hết API.

- Public: `/actuator/health`, `/actuator/info`
- Các API file dưới `/api/v1/files/**` mặc định **cần Authorization**:
  - Header: `Authorization: Bearer <JWT>`

Nếu bạn muốn test nhanh không cần JWT ở local, bạn có thể tạm thời permit các route trong `WebSecurityConfig` (không khuyến nghị cho production).

## Danh sách API
Base URL (local): `http://localhost:9106`

- **GET** `/api/v1/files/health`
- **POST** `/api/v1/files/upload` (multipart/form-data)
- **POST** `/api/v1/files/uploads` (multipart/form-data, upload nhiều file)
- **GET** `/api/v1/files/download?key=...` (stream file)
- **GET** `/api/v1/files/meta?key=...`
- **DELETE** `/api/v1/files?key=...`
- **POST** `/api/v1/files/copy?fromKey=...&toKey=...`
- **POST** `/api/v1/files/rename?fromKey=...&toKey=...` (copy + delete)
- **GET** `/api/v1/files/presign/get?key=...&expiresInSeconds=...`
- **POST** `/api/v1/files/presign/put`
- **GET** `/api/v1/files/list?prefix=&continuationToken=&maxKeys=`

## Test trên Postman (step-by-step)

### 1) Tạo Environment
Tạo 1 environment (ví dụ `local`) với variables:
- `baseUrl`: `http://localhost:9106`
- `token`: JWT của bạn (nếu cần)
- `fileKey`: để lưu key trả về sau upload

### 2) Set Authorization (cho các request cần auth)
Trong từng request (hoặc collection), tab **Authorization**:
- Type: `Bearer Token`
- Token: `{{token}}`

Hoặc set header thủ công:
- Key: `Authorization`
- Value: `Bearer {{token}}`

### 3) Health check bucket
**Request**
- Method: `GET`
- URL: `{{baseUrl}}/api/v1/files/health`

**Expected**
- Status `200 OK` nếu bucket truy cập được.

### 4) Upload file
- **Upload single file**
**Request**
- Method: `POST`
- URL: `{{baseUrl}}/api/v1/files/upload?prefix=story-assets`
- Body → `form-data`
  - Key: `file` (type **File**) → chọn file từ máy

**Expected Response (ví dụ)**
```json
{
  "bucket": "ph-story-bucket",
  "key": "story-assets/2026/03/18/<uuid>_my-photo.png",
  "eTag": "\"...\"",
  "size": 12345,
  "contentType": "image/png"
}
```

**Tip**
- Copy field `key` và lưu vào environment `fileKey` để test các API tiếp theo.

- **Upload multiple files**
**Request**
- Method: `POST`
- URL: `{{baseUrl}}/api/v1/files/uploads?prefix=story-assets`
- Body → `form-data`
  - Key: `files` (type **File**) → chọn nhiều file cùng lúc

**Expected Response**
Trả về JSON dạng list `FileUploadResponse` (mỗi file 1 phần tử), mỗi phần tử có `key` để bạn lưu vào Postman environment và test các API tiếp theo.

### 5) Meta (headObject)
**Request**
- Method: `GET`
- URL: `{{baseUrl}}/api/v1/files/meta?key={{fileKey}}`

**Expected**
- Trả về `contentLength`, `contentType`, `lastModified`, `metadata`, ...

### 6) Download (stream / force download)
**Mục đích**: Tải nội dung file, có thể **xem trực tiếp** (inline) hoặc **ép tải xuống** (attachment), đồng thời hỗ trợ **cache** bằng `ETag`.

**Request**
- Method: `GET`
- URL: `{{baseUrl}}/api/v1/files/download?key={{fileKey}}`
- URL full: `{{baseUrl}}/api/v1/files/download?key={{fileKey}}&download=false&cacheSeconds=0`

**Query params**
- `download` (optional, default `false`)
  - `false` → header `Content-Disposition: inline; ...` → trình duyệt có thể mở trực tiếp (ảnh, pdf, video,...).
  - `true` → header `Content-Disposition: attachment; ...` → trình duyệt sẽ hiện hộp thoại “Save as…”.
- `cacheSeconds` (optional, default `0`)
  - `0` → **không set** `Cache-Control` (mặc định để browser tự quyết, phù hợp nội dung private).
  - `>0` → server set header `Cache-Control: private, max-age=<cacheSeconds>` để browser cache trong `<cacheSeconds>` giây.

**ETag / tránh tải lại file không đổi**
- Response có thể trả header `ETag` (do SeaweedFS S3 cung cấp).
- Lần sau nếu client gửi kèm header:
  - `If-None-Match: <etag-cũ>`
- Nếu nội dung file chưa đổi (ETag giống nhau), server trả:
  - Status `304 Not Modified` **không có body**, browser sẽ dùng lại bản cache sẵn có → giảm băng thông.

**Cách lưu file bằng Postman**
- Click mũi tên cạnh nút **Send** → chọn **Send and Download** để lưu file xuống máy.

### 7) List objects (liệt kê file trong bucket)
**Mục đích**: Xem danh sách file theo `prefix` (kiểu thư mục ảo).

**Request**
- Method: `GET`
- URL:  
  `{{baseUrl}}/api/v1/files/list?prefix=story-assets/&maxKeys=100`

**Query params**
- `prefix` (optional): chỉ liệt kê các key bắt đầu với chuỗi này, ví dụ `story-assets/2026/03/18/`.
- `maxKeys` (optional): số lượng tối đa mỗi trang (`1..1000`, default `100`).
- `continuationToken` (optional): token để lấy **trang tiếp theo**.

**Response (rút gọn)**
```json
{
  "bucket": "ph-story-bucket",
  "prefix": "story-assets/",
  "truncated": true,
  "nextContinuationToken": "xxxx",
  "items": [
    { "key": "story-assets/a.png", "size": 1234, "eTag": "\"...\"", "lastModified": "2026-03-18T..." }
  ]
}
```

**Paging**
- Nếu `truncated = true` → lấy `nextContinuationToken` rồi gọi lại:
  - `{{baseUrl}}/api/v1/files/list?prefix=story-assets/&continuationToken={{nextContinuationToken}}`

---

### 8) Presign GET (tạo URL tải file, client không cần JWT)
**Mục đích**: Tạo **URL tạm thời** để client tải file trực tiếp từ SeaweedFS S3 (không đi qua service nữa).

**Request**
- Method: `GET`
- URL:  
  `{{baseUrl}}/api/v1/files/presign/get?key={{fileKey}}&expiresInSeconds=900`

**Query params**
- `key`: key file trong bucket (ví dụ lấy từ `upload` hoặc `list`).
- `expiresInSeconds` (optional, default cấu hình): thời gian sống của link, ví dụ `900` (15 phút).

**Response**
```json
{
  "method": "GET",
  "url": "http://seaweed-s3-gw/...signed...",
  "expiresInSeconds": 900
}
```

**Cách dùng**
- Tạo request mới trong Postman:
  - Method: **GET**
  - URL: giá trị `url` trong response
  - Thường **không cần Authorization**, vì đã sign sẵn.

---

### 9) Presign PUT (tạo URL cho client tự upload file)
**Mục đích**: Cho phép FE/mobile **upload trực tiếp** lên SeaweedFS S3 (không đi qua body của service).

**Bước 1 – Lấy presigned PUT**
- Method: `POST`
- URL: `{{baseUrl}}/api/v1/files/presign/put`
- Header: `Content-Type: application/json`
- Body:
```json
{
  "key": "story-assets/client-uploads/demo.png",
  "contentType": "image/png",
  "expiresInSeconds": 900
}
```

**Response**
```json
{
  "method": "PUT",
  "url": "http://seaweed-s3-gw/...signed...",
  "expiresInSeconds": 900
}
```

**Bước 2 – Upload file bằng presigned URL**
Tạo một request mới:
- Method: `PUT`
- URL: giá trị `url` trả về ở trên
- Headers:
  - `Content-Type: image/png` (nên đúng với lúc bạn gửi trong step 1)
- Body:
  - Chọn **binary** → chọn file từ máy

---

### 10) Copy / Rename / Delete

#### 10.1) Copy file
**Mục đích**: Tạo bản sao của file với key mới (giữ nguyên nội dung/metadata).

**Request**
- Method: `POST`
- URL:  
  `{{baseUrl}}/api/v1/files/copy?fromKey={{fileKey}}&toKey=story-assets/copied.png`

#### 10.2) Rename (move) file
**Mục đích**: Đổi đường dẫn/key file (thực hiện `copy` rồi `delete`).

**Request**
- Method: `POST`
- URL:  
  `{{baseUrl}}/api/v1/files/rename?fromKey={{fileKey}}&toKey=story-assets/moved.png`

#### 10.3) Delete file
**Request**
- Method: `DELETE`
- URL:  
  `{{baseUrl}}/api/v1/files?key={{fileKey}}`

**Expected**
- Status `200 OK` (hoặc `204 No Content` tuỳ client hiển thị).

## Lỗi thường gặp
- **401/403**: thiếu `Authorization: Bearer ...` hoặc JWT issuer/audience không đúng.
- **400 VALIDATION_ERROR**: sai extension, file quá lớn, hoặc key không hợp lệ (bắt đầu bằng `/` hoặc có `..`).
- **500 SeaweedFS bucket not accessible**: sai endpoint/bucket/credential hoặc SeaweedFS S3 Gateway chưa chạy.

