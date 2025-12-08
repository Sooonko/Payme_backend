package com.itwizard.payme.dto.response;

import com.itwizard.payme.domain.enums.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private UUID id;
    private String cardHolderName;
    private String cardNumberLast4;
    private CardType cardType;
    private Integer expiryMonth;
    private Integer expiryYear;
    private Boolean isDefault;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    // Display helper for card info
    public String getDisplayName() {
        return cardType + " •••• " + cardNumberLast4;
    }
}
