package com.ph.core.story.infrastructure.client;

import com.ph.core.story.common.config.SeaweedFsClientProperties;
import com.ph.core.story.common.exception.BaseBusinessException;
import com.ph.core.story.common.exception.ErrorCode;
import com.ph.core.story.infrastructure.client.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SeaweedFsStorageClient {

    private final RestClient.Builder restClientBuilder;
    private final SeaweedFsClientProperties properties;

    public FileUploadResponse upload(MultipartFile file, String prefix, String authorizationHeader) {
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            HttpHeaders partHeaders = new HttpHeaders();
            partHeaders.setContentDispositionFormData("file", file.getOriginalFilename());
            partHeaders.setContentType(resolveContentType(file));

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new HttpEntity<>(resource, partHeaders));

            return restClient().post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/api/v1/files/upload");
                        if (StringUtils.hasText(prefix)) {
                            builder.queryParam("prefix", prefix);
                        }
                        return builder.build();
                    })
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .headers(headers -> applyAuthorization(headers, authorizationHeader))
                    .body(body)
                    .retrieve()
                    .body(FileUploadResponse.class);
        } catch (IOException ex) {
            throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Cannot read avatar file for upload");
        } catch (RestClientResponseException ex) {
            throw new BaseBusinessException(ErrorCode.BAD_REQUEST,
                    "Avatar upload failed: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Avatar upload integration failed");
        }
    }

    public void delete(String key, String authorizationHeader) {
        if (!StringUtils.hasText(key)) {
            return;
        }

        try {
            restClient().delete()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/files")
                            .queryParam("key", key)
                            .build())
                    .headers(headers -> applyAuthorization(headers, authorizationHeader))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw new BaseBusinessException(ErrorCode.BAD_REQUEST,
                    "Avatar cleanup failed: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new BaseBusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "Avatar cleanup integration failed");
        }
    }

    private RestClient restClient() {
        return restClientBuilder.baseUrl(properties.getBaseUrl()).build();
    }

    private void applyAuthorization(HttpHeaders headers, String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader)) {
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
    }

    private MediaType resolveContentType(MultipartFile file) {
        if (!StringUtils.hasText(file.getContentType())) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        return MediaType.parseMediaType(file.getContentType());
    }
}
