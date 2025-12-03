package com.itwizard.payme.controller;

import com.itwizard.payme.dto.request.TopupRequest;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.dto.response.TopupResponse;
import com.itwizard.payme.security.CurrentUser;
import com.itwizard.payme.security.UserPrincipal;
import com.itwizard.payme.service.TopupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topup")
@RequiredArgsConstructor
public class TopupController {

    private final TopupService topupService;

    @PostMapping("/initiate")
    public ResponseEntity<StandardResponse<TopupResponse>> initiateTopup(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @Valid @RequestBody TopupRequest request) {
        TopupResponse response = topupService.initiateTopup(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(response, "Top-up initiated successfully"));
    }

    @PostMapping("/confirm/{transactionId}")
    public ResponseEntity<StandardResponse<TopupResponse>> confirmTopup(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @PathVariable Long transactionId) {
        TopupResponse response = topupService.confirmTopup(userPrincipal.getId(), transactionId);
        return ResponseEntity.ok(StandardResponse.success(response, "Top-up completed successfully"));
    }

    @GetMapping("/history")
    public ResponseEntity<StandardResponse<List<TopupResponse>>> getTopupHistory(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        List<TopupResponse> response = topupService.getTopupHistory(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(response, "Top-up history retrieved successfully"));
    }
}
