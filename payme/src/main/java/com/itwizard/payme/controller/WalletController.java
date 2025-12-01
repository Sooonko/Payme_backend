package com.itwizard.payme.controller;

import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.WalletResponse;
import com.itwizard.payme.security.JwtTokenProvider;
import com.itwizard.payme.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/my-wallet")
    public ResponseEntity<StandardResponse<WalletResponse>> getMyWallet(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        UUID userId = jwtTokenProvider.getUserIdFromToken(jwt);
        WalletResponse response = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(StandardResponse.success(response, "Wallet retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<WalletResponse>> getWalletById(@PathVariable UUID id) {
        WalletResponse response = walletService.getWalletById(id);
        return ResponseEntity.ok(StandardResponse.success(response, "Wallet retrieved successfully"));
    }
}
