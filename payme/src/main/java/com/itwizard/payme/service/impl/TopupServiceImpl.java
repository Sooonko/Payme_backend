package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.ExternalTransaction;
import com.itwizard.payme.domain.Wallet;
import com.itwizard.payme.domain.enums.TransactionStatus;
import com.itwizard.payme.dto.request.TopupRequest;
import com.itwizard.payme.dto.response.TopupResponse;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.ExternalTransactionRepository;
import com.itwizard.payme.repository.WalletRepository;
import com.itwizard.payme.service.AuditService;
import com.itwizard.payme.service.TopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopupServiceImpl implements TopupService {

        private final ExternalTransactionRepository externalTransactionRepository;
        private final WalletRepository walletRepository;
        private final AuditService auditService;

        @Override
        @Transactional
        public TopupResponse initiateTopup(UUID userId, TopupRequest request) {
                // Get user's wallet
                Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

                // Direct Demo Top-up: Immediately credit the account

                // Create external transaction (COMPLETED)
                ExternalTransaction transaction = new ExternalTransaction();
                transaction.setWallet(wallet);
                transaction.setUser(wallet.getUser());
                transaction.setAmount(request.getAmount());
                transaction.setStatus(TransactionStatus.COMPLETED.name());
                transaction.setProvider("QPAY_DEMO");
                transaction.setExternalRefId(UUID.randomUUID().toString());
                transaction.setQpayInvoiceId("DEMO_INVOICE_" + System.currentTimeMillis());
                transaction.setQpayQrText("NO_QR_DEMO_MODE");

                // Update Wallet Balance immediately
                wallet.setBalance(wallet.getBalance().add(request.getAmount()));
                walletRepository.save(wallet);

                transaction = externalTransactionRepository.save(transaction);

                // Log audit
                auditService.logAction(userId, "TOPUP_COMPLETED",
                                "Top-up completed (Demo): " + request.getAmount(), null);

                // Return response
                return TopupResponse.builder()
                                .transactionId(transaction.getId())
                                .amount(transaction.getAmount())
                                .status(TransactionStatus.valueOf(transaction.getStatus()))
                                .provider(transaction.getProvider())
                                .qrCode(null)
                                .createdAt(transaction.getCreatedAt())
                                .build();
        }

        @Override
        @Transactional
        public TopupResponse confirmTopup(UUID userId, Long transactionId) { // Changed to Long
                // Get transaction
                ExternalTransaction transaction = externalTransactionRepository.findById(transactionId)
                                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

                // Verify it belongs to user's wallet
                if (!transaction.getWallet().getUser().getId().equals(userId)) {
                        throw new IllegalArgumentException("Transaction does not belong to this user");
                }

                // Check if already completed
                if (TransactionStatus.COMPLETED.name().equals(transaction.getStatus())) {
                        throw new IllegalStateException("Transaction already completed");
                }

                // Verify with QPay
                // Verify with QPay - DISABLED for Demo Mode
                // if ("QPAY".equals(transaction.getProvider())) {
                // boolean isPaid = qPayService.checkPayment(transaction.getQpayInvoiceId());
                // if (!isPaid) {
                // throw new IllegalStateException("Payment not verified by QPay");
                // }
                // }

                // Update wallet balance
                Wallet wallet = transaction.getWallet();
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                walletRepository.save(wallet);

                // Update transaction status
                transaction.setStatus(TransactionStatus.COMPLETED.name());
                transaction = externalTransactionRepository.save(transaction);

                // Log audit
                auditService.logAction(userId, "TOPUP_COMPLETED",
                                "Top-up completed: " + transaction.getAmount(), null);

                return TopupResponse.builder()
                                .transactionId(transaction.getId())
                                .amount(transaction.getAmount())
                                .status(TransactionStatus.valueOf(transaction.getStatus()))
                                .provider(transaction.getProvider())
                                .qrCode(null)
                                .createdAt(transaction.getCreatedAt())
                                .build();
        }

        @Override
        public List<TopupResponse> getTopupHistory(UUID userId) {
                // Get user's wallet
                Wallet wallet = walletRepository.findByUserId(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

                // Get all top-up transactions
                List<ExternalTransaction> transactions = externalTransactionRepository
                                .findByWalletIdOrderByCreatedAtDesc(wallet.getId());

                return transactions.stream()
                                // Filter by provider to ensure we only get top-ups if we use other providers
                                // later
                                // For now, assuming all external transactions for this wallet are relevant
                                .map(t -> TopupResponse.builder()
                                                .transactionId(t.getId())
                                                .amount(t.getAmount())
                                                .status(TransactionStatus.valueOf(t.getStatus()))
                                                .provider(t.getProvider())
                                                .createdAt(t.getCreatedAt())
                                                .build())
                                .toList();
        }
}
