package com.itwizard.payme.domain.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Authentication & Authorization
    INVALID_CREDENTIALS("AUTH001", "Invalid email or password"),
    UNAUTHORIZED("AUTH002", "Unauthorized access"),
    TOKEN_EXPIRED("AUTH003", "Authentication token has expired"),

    // User errors
    USER_NOT_FOUND("USER001", "User not found"),
    USER_ALREADY_EXISTS("USER002", "User already exists"),
    EMAIL_ALREADY_REGISTERED("USER003", "Email already registered"),
    PHONE_ALREADY_REGISTERED("USER004", "Phone number already registered"),

    // Wallet errors
    WALLET_NOT_FOUND("WALLET001", "Wallet not found"),
    INSUFFICIENT_BALANCE("WALLET002", "Insufficient balance"),

    // Transaction errors
    TRANSACTION_FAILED("TXN001", "Transaction failed"),
    INVALID_AMOUNT("TXN002", "Invalid transaction amount"),
    TRANSACTION_NOT_FOUND("TXN003", "Transaction not found"),

    // System errors
    INTERNAL_SERVER_ERROR("SYS001", "Internal server error"),
    VALIDATION_ERROR("SYS002", "Validation error"),
    BAD_REQUEST("SYS003", "Bad request"),
    RATE_LIMIT_EXCEEDED("SYS004", "Rate limit exceeded");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
