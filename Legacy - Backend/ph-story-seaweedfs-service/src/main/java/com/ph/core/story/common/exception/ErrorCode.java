package com.ph.core.story.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =============================
    // 4xx - Client Errors
    // =============================
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST),
    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED),
    DATA_CONFLICT("DATA_CONFLICT", HttpStatus.CONFLICT),

    // =============================
    // 5xx - Server Errors
    // =============================
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final HttpStatus status;
}

