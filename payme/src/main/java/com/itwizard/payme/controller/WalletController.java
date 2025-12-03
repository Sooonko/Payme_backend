package com.itwizard.payme.controller;

import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.WalletResponse;
import com.itwizard.payme.security.CurrentUser;
import com.itwizard.payme.security.UserPrincipal;
import com.itwizard.payme.service.WalletService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/my-wallet")
    public ResponseEntity<StandardResponse<WalletResponse>> getMyWallet(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        WalletResponse response = walletService.getWalletByUserId(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(response, "Wallet retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<WalletResponse>> getWalletById(@PathVariable UUID id) {
        WalletResponse response = walletService.getWalletById(id);
        return ResponseEntity.ok(StandardResponse.success(response, "Wallet retrieved successfully"));
    }
}
