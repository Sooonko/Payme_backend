package com.itwizard.payme.controller;

import com.itwizard.payme.dto.request.TransactionRequest;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.TransactionResponse;
import com.itwizard.payme.security.CurrentUser;
import com.itwizard.payme.security.UserPrincipal;
import com.itwizard.payme.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/send")
    public ResponseEntity<StandardResponse<TransactionResponse>> sendMoney(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.sendMoney(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(response, "Transaction completed successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<StandardResponse<List<TransactionResponse>>> getTransactionHistory(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        List<TransactionResponse> response = transactionService.getTransactionHistory(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(response, "Transaction history retrieved successfully"));
    }

    @GetMapping("/recent")
    public ResponseEntity<StandardResponse<List<TransactionResponse>>> getRecentTransactions(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        List<TransactionResponse> response = transactionService.getRecentTransactions(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(response, "Recent transactions retrieved successfully"));
    }
}
