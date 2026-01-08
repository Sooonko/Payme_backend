package com.itwizard.payme.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductOption {
    private UUID productId;
    private String productName;
    private String description;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal maxEligibleAmount;
    private BigDecimal interestRateMonthly;
    private BigDecimal penaltyRateDaily;
    private Integer minTenorMonths;
    private Integer maxTenorMonths;
    private String statusMessage;
    private boolean isChecked;
}
