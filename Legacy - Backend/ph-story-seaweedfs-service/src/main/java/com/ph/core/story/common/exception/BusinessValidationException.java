package com.ph.core.story.common.exception;

public class BusinessValidationException extends BaseBusinessException {

    public BusinessValidationException(String message, Object details) {
        super(ErrorCode.VALIDATION_ERROR, message, details);
    }
}

