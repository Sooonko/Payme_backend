package com.itwizard.payme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private UUID id;
    private UUID userId;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
}
