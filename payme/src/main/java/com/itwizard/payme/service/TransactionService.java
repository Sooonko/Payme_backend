package com.itwizard.payme.service;

import com.itwizard.payme.dto.request.TransactionRequest;
import com.itwizard.payme.dto.response.TransactionResponse;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse sendMoney(UUID fromUserId, TransactionRequest request);

    List<TransactionResponse> getTransactionHistory(UUID userId);

    List<TransactionResponse> getRecentTransactions(UUID userId);
}
