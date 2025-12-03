package com.itwizard.payme.service;

import com.itwizard.payme.dto.response.WalletResponse;

import java.util.UUID;

public interface WalletService {
    WalletResponse getWalletByUserId(UUID userId);

    WalletResponse getWalletById(UUID walletId);

    WalletResponse createWallet(UUID userId, String currency);
}
