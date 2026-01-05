package com.itwizard.payme.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01", message = "Requested amount must be greater than zero")
    private BigDecimal requestedAmount;

    @NotNull(message = "Tenor months is required")
    @Min(value = 1, message = "Tenor months must be at least 1")
    private Integer tenorMonths;
}
