package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.Transaction;
import com.itwizard.payme.domain.Wallet;
import com.itwizard.payme.domain.enums.TransactionStatus;
import com.itwizard.payme.domain.enums.TransactionType;
import com.itwizard.payme.dto.request.TransactionRequest;
import com.itwizard.payme.dto.response.TransactionResponse;
import com.itwizard.payme.exception.InsufficientBalanceException;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.TransactionRepository;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.repository.WalletRepository;
import com.itwizard.payme.service.AuditService;
import com.itwizard.payme.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @org.springframework.beans.factory.annotation.Value("${payme.transaction.max-amount}")
    private BigDecimal maxTransactionAmount;

    @org.springframework.beans.factory.annotation.Value("${payme.transaction.daily-limit}")
    private BigDecimal dailyTransactionLimit;

    @Override
    @Transactional
    public TransactionResponse sendMoney(UUID fromUserId, TransactionRequest request) {
        // 0. Check transaction limits
        if (request.getAmount().compareTo(maxTransactionAmount) > 0) {
            throw new com.itwizard.payme.exception.TransactionLimitExceededException(
                    "Transaction amount exceeds limit of " + maxTransactionAmount);
        }

        // Check daily limit
        BigDecimal todayTotal = transactionRepository.calculateDailyTotal(fromUserId,
                java.time.LocalDateTime.now().toLocalDate().atStartOfDay());
        if (todayTotal == null)
            todayTotal = BigDecimal.ZERO;

        if (todayTotal.add(request.getAmount()).compareTo(dailyTransactionLimit) > 0) {
            throw new com.itwizard.payme.exception.TransactionLimitExceededException(
                    "Daily transaction limit of " + dailyTransactionLimit + " exceeded");
        }

        // 1. Validate sender wallet
        Wallet fromWallet = walletRepository.findByUserId(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender wallet not found"));

        // 2. Validate receiver wallet
        Wallet toWallet = walletRepository.findById(request.getToWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver wallet not found"));

        // 3. Check if sending to self
        if (fromWallet.getId().equals(toWallet.getId())) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }

        // 4. Check balance
        if (fromWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // 5. Perform transfer
        fromWallet.setBalance(fromWallet.getBalance().subtract(request.getAmount()));
        toWallet.setBalance(toWallet.getBalance().add(request.getAmount()));

        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);

        // 6. Create transaction record
        Transaction transaction = new Transaction();
        transaction.setFromWallet(fromWallet);
        transaction.setToWallet(toWallet);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.SEND);
        transaction.setStatus(TransactionStatus.COMPLETED);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // 7. Log audit
        auditService.logAction(fromUserId, "SEND_MONEY",
                String.format("Sent %s to wallet %s", request.getAmount(), toWallet.getId()), null);

        return mapToResponse(savedTransaction);
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(UUID userId) {
        // Get user's wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Get all transactions
        List<Transaction> transactions = transactionRepository.findByWalletId(wallet.getId());

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getRecentTransactions(UUID userId) {
        // Get user's wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Get recent transactions (last 10)
        List<Transaction> transactions = transactionRepository.findTop10ByWalletIdOrderByCreatedAtDesc(wallet.getId());

        // Limit to 10 results
        return transactions.stream()
                .limit(10)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getFromWallet() != null ? transaction.getFromWallet().getId() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getId() : null,
                transaction.getAmount(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getDescription(),
                transaction.getCreatedAt());
    }
}
