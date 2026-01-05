package com.itwizard.payme.dto.response;

import com.itwizard.payme.domain.enums.LoanApplicationStatus;
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
public class LoanApplicationResponse {
    private UUID applicationId;
    private UUID userId;
    private UUID productId;
    private BigDecimal requestedAmount;
    private BigDecimal maxEligibleAmount;
    private Integer tenorMonths;
    private LoanApplicationStatus status;
    private String message;
    private LocalDateTime createdAt;
}
