package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.User;
import com.itwizard.payme.domain.Wallet;
import com.itwizard.payme.dto.response.WalletResponse;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.repository.WalletRepository;
import com.itwizard.payme.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

        private final WalletRepository walletRepository;
        private final UserRepository userRepository;

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

        @Override
        @Transactional
        public WalletResponse createWallet(UUID userId, String currency) {
                // Check if user exists
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

                // Check if wallet already exists
                if (walletRepository.findByUserId(userId).isPresent()) {
                        throw new IllegalStateException("Wallet already exists for user: " + userId);
                }

                // Create new wallet with 0 balance
                Wallet wallet = new Wallet();
                wallet.setUser(user);
                wallet.setBalance(BigDecimal.ZERO);
                wallet.setCurrency(currency != null ? currency : "MNT");

                wallet = walletRepository.save(wallet);

                return new WalletResponse(
                                wallet.getId(),
                                wallet.getUser().getId(),
                                wallet.getBalance(),
                                wallet.getCurrency(),
                                wallet.getCreatedAt());
        }
}
