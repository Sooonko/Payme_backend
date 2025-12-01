package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.Wallet;
import com.itwizard.payme.dto.response.WalletResponse;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.WalletRepository;
import com.itwizard.payme.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

        private final WalletRepository walletRepository;

        @Override
        public WalletResponse getWalletByUserId(UUID userId) {
                Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Wallet not found for user: " + userId));

                return new WalletResponse(
                                wallet.getId(),
                                wallet.getUser().getId(),
                                wallet.getBalance(),
                                wallet.getCurrency(),
                                wallet.getCreatedAt());
        }

        @Override
        public WalletResponse getWalletById(UUID walletId) {
                Wallet wallet = walletRepository.findById(walletId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Wallet not found with id: " + walletId));

                return new WalletResponse(
                                wallet.getId(),
                                wallet.getUser().getId(),
                                wallet.getBalance(),
                                wallet.getCurrency(),
                                wallet.getCreatedAt());
        }
}
