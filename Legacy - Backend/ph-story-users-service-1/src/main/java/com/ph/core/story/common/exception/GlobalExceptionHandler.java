package com.ph.core.story.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.ph.core.story.common.response.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ======================================================
    // Business Exception
    // ======================================================
    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BaseBusinessException ex,
            HttpServletRequest request) {

        ErrorCode errorCode = ex.getErrorCode();

        log.warn("Business exception [{}]: {}", errorCode.getCode(), ex.getMessage());

        return buildResponse(errorCode, ex.getMessage(), request, ex.getDetails());
    }

    // ======================================================
    // Validation - @RequestBody
    // ======================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));

        log.warn("Validation failed: {}", validationErrors);

        return buildResponse(ErrorCode.VALIDATION_ERROR, "Validation failed", request,
                validationErrors);
    }

    // ======================================================
    // Validation - @RequestParam, @PathVariable
    // ======================================================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage, (existing, replacement) -> existing));

        log.warn("Constraint violation: {}", errors);

        return buildResponse(ErrorCode.VALIDATION_ERROR, "Validation failed", request, errors);
    }

    // ======================================================
    // Optimistic Lock
    // ======================================================
    @ExceptionHandler({ObjectOptimisticLockingFailureException.class,
            OptimisticLockException.class})
    public ResponseEntity<ApiError> handleOptimisticLock(Exception ex, HttpServletRequest request) {

        log.warn("Optimistic lock conflict", ex);

        return buildResponse(ErrorCode.DATA_CONFLICT,
                "The data has been modified by another user. Please refresh and try again.",
                request, null);
    }

    // ======================================================
    // Generic Exception (System Error)
    // ======================================================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {

        log.error("Unexpected system error", ex);

        return buildResponse(ErrorCode.INTERNAL_SERVER_ERROR, "Something went wrong", request,
                null);
    }

    // ======================================================
    // Enum error (MethodArgumentTypeMismatchException)
    // ======================================================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String message =
                String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());

        log.warn("Type mismatch: {}", message);

        return buildResponse(ErrorCode.VALIDATION_ERROR, message, request, null);
    }

    // ======================================================
    // Bắt lỗi JSON sai / Enum parse lỗi (HttpMessageNotReadableException)
    // ======================================================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String message = "Malformed JSON request";

        if (ex.getCause() instanceof InvalidFormatException ife) {

            String fieldName = ife.getPath().stream().map(ref -> ref.getFieldName())
                    .reduce((first, second) -> second).orElse("unknown");

            message = String.format("Invalid value '%s' for field '%s'", ife.getValue(), fieldName);
        }

        log.warn("Unreadable JSON: {}", message);

        return buildResponse(ErrorCode.VALIDATION_ERROR, message, request, null);
    }

    // ======================================================
    // Central Response Builder
    // ======================================================
    private ResponseEntity<ApiError> buildResponse(ErrorCode errorCode, String message,
            HttpServletRequest request, Object details) {

        ApiError error = ApiError.builder().code(errorCode.getCode()).message(message)
                .status(errorCode.getStatus().value()).path(request.getRequestURI())
                .details(details).timestamp(Instant.now()).traceId(getTraceId()).build();

        return ResponseEntity.status(errorCode.getStatus()).body(error);
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }
}
