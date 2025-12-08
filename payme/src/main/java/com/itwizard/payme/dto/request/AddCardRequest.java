package com.itwizard.payme.dto.request;

import com.itwizard.payme.domain.enums.CardType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCardRequest {

    @NotBlank(message = "Card holder name is required")
    @Size(max = 255, message = "Card holder name must not exceed 255 characters")
    private String cardHolderName;

    @NotBlank(message = "Card number last 4 digits is required")
    @Pattern(regexp = "^\\d{4}$", message = "Card number last 4 must be exactly 4 digits")
    private String cardNumberLast4;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    @NotNull(message = "Expiry month is required")
    @Min(value = 1, message = "Expiry month must be between 1 and 12")
    @Max(value = 12, message = "Expiry month must be between 1 and 12")
    private Integer expiryMonth;

    @NotNull(message = "Expiry year is required")
    @Min(value = 2024, message = "Expiry year must be in the future")
    private Integer expiryYear;

    // Token from payment gateway (Stripe, QPay, etc.)
    @NotBlank(message = "Card token is required")
    private String cardToken;

    private Boolean isDefault;
}
