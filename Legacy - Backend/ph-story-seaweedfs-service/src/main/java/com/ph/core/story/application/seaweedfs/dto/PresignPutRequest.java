package com.ph.core.story.application.seaweedfs.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresignPutRequest {

    @NotBlank
    private String key;

    /**
     * Optional content-type you expect the client to upload.
     * If provided, the signed request will require this content-type.
     */
    private String contentType;

    @Min(10)
    @Max(86400)
    private Long expiresInSeconds;
}

