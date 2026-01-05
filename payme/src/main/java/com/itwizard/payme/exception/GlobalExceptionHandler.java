package com.itwizard.payme.exception;

import com.itwizard.payme.domain.enums.ErrorCode;
import com.itwizard.payme.dto.response.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardResponse.error(
                        "Endpoint not found or method not supported",
                        ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(StandardResponse.error(ex.getMessage(), ErrorCode.USER_NOT_FOUND));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<StandardResponse<Void>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(StandardResponse.error(ex.getMessage(), ErrorCode.INVALID_CREDENTIALS));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<StandardResponse<Void>> handleInsufficientBalanceException(
            InsufficientBalanceException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.error(ex.getMessage(), ErrorCode.INSUFFICIENT_BALANCE));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGlobalException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponse.error(
                        "An unexpected error occurred: " + ex.getMessage(),
                        ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
