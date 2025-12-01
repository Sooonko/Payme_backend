package com.itwizard.payme.dto.response;

import com.itwizard.payme.domain.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponse<T> {

    /**
     * Indicates if the request was successful
     */
    private boolean success;

    /**
     * Human-readable message describing the result
     */
    private String message;

    /**
     * Application-specific error code for client handling (nullable)
     */
    private ErrorCode errorCode;

    /**
     * The actual data payload (nullable)
     */
    private T data;

    /**
     * Timestamp when the response was generated
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Additional metadata like pagination info (nullable)
     */
    private Object metadata;

    // Static factory methods for common response patterns

    /**
     * Create a successful response with data and custom message
     */
    public static <T> StandardResponse<T> success(T data, String message) {
        return StandardResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a successful response with data
     */
    public static <T> StandardResponse<T> success(T data) {
        return StandardResponse.<T>builder()
                .success(true)
                .message("Operation completed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a successful response without data
     */
    public static <T> StandardResponse<T> success(String message) {
        return StandardResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response with error code
     */
    public static <T> StandardResponse<T> error(String message, ErrorCode errorCode) {
        return StandardResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create an error response with data and error code
     */
    public static <T> StandardResponse<T> error(T data, String message, ErrorCode errorCode) {
        return StandardResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create a response with metadata (for pagination, etc.)
     */
    public static <T> StandardResponse<T> successWithMetadata(T data, String message, Object metadata) {
        return StandardResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
