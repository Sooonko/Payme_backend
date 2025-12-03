package com.itwizard.payme.dto.response;

import com.itwizard.payme.domain.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopupResponse {
    private Long transactionId;
    private BigDecimal amount;
    private TransactionStatus status;
    private String provider;
    private String qrCode;
    private LocalDateTime createdAt;
}
