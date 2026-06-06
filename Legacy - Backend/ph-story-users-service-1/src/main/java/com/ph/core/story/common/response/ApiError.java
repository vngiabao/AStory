package com.ph.core.story.common.response;

//public record ApiError(
//    int status,
//    String message,
//    Instant timestamp
//) {}

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ApiError {

    private String code;         // BUSINESS_001
    private String message;
    private int status;
    private String path;
    private Instant timestamp;
    private Object details;      // validation errors
    private String traceId; // optional cho distributed system
}


