package com.itwizard.payme.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Monthly interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    private BigDecimal interestRateMonthly;

    @NotNull(message = "Daily penalty rate is required")
    @DecimalMin(value = "0.0", message = "Penalty rate cannot be negative")
    private BigDecimal penaltyRateDaily;

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.01", message = "Minimum amount must be greater than zero")
    private BigDecimal minAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.01", message = "Maximum amount must be greater than zero")
    private BigDecimal maxAmount;

    @NotNull(message = "Minimum tenor months is required")
    @Min(value = 1, message = "Minimum tenor must be at least 1 month")
    private Integer minTenorMonths;

    @NotNull(message = "Maximum tenor months is required")
    @Min(value = 1, message = "Maximum tenor must be at least 1 month")
    private Integer maxTenorMonths;

    @NotNull(message = "Scoring multiplier is required")
    @DecimalMin(value = "0.01", message = "Scoring multiplier must be greater than zero")
    private BigDecimal scoringMultiplier;
}
