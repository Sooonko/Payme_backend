package com.itwizard.payme.controller;

import com.itwizard.payme.dto.request.TransactionRequest;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.TransactionResponse;
import com.itwizard.payme.security.JwtTokenProvider;
import com.itwizard.payme.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send")
    public ResponseEntity<StandardResponse<TransactionResponse>> sendMoney(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TransactionRequest request) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        UUID fromUserId = jwtTokenProvider.getUserIdFromToken(jwt);
        TransactionResponse response = transactionService.sendMoney(fromUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(response, "Transaction completed successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<StandardResponse<List<TransactionResponse>>> getTransactionHistory(
            @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        UUID userId = jwtTokenProvider.getUserIdFromToken(jwt);
        List<TransactionResponse> response = transactionService.getTransactionHistory(userId);
        return ResponseEntity.ok(StandardResponse.success(response, "Transaction history retrieved successfully"));
    }

    @GetMapping("/recent")
    public ResponseEntity<StandardResponse<List<TransactionResponse>>> getRecentTransactions(
            @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        UUID userId = jwtTokenProvider.getUserIdFromToken(jwt);
        List<TransactionResponse> response = transactionService.getRecentTransactions(userId);
        return ResponseEntity.ok(StandardResponse.success(response, "Recent transactions retrieved successfully"));
    }
}
