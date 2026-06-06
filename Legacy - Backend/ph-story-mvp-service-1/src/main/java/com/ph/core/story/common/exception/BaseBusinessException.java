package com.ph.core.story.common.exception;

import lombok.Getter;

@Getter
public class BaseBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object details;

    public BaseBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public BaseBusinessException(ErrorCode errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}

