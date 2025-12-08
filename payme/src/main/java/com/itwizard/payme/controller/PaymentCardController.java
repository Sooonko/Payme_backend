package com.itwizard.payme.controller;

import com.itwizard.payme.dto.request.AddCardRequest;
import com.itwizard.payme.dto.response.CardResponse;
import com.itwizard.payme.dto.response.StandardResponse;
import com.itwizard.payme.security.CurrentUser;
import com.itwizard.payme.security.UserPrincipal;
import com.itwizard.payme.service.PaymentCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    /**
     * Add a new payment card
     */
    @PostMapping
    public ResponseEntity<StandardResponse<CardResponse>> addCard(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @Valid @RequestBody AddCardRequest request) {
        CardResponse response = paymentCardService.addCard(userPrincipal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponse.success(response, "Card added successfully"));
    }

    /**
     * Get all payment cards for current user
     */
    @GetMapping
    public ResponseEntity<StandardResponse<List<CardResponse>>> getUserCards(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        List<CardResponse> cards = paymentCardService.getUserCards(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(cards, "Cards retrieved successfully"));
    }

    /**
     * Get default payment card
     */
    @GetMapping("/default")
    public ResponseEntity<StandardResponse<CardResponse>> getDefaultCard(
            @CurrentUser @NotNull UserPrincipal userPrincipal) {
        CardResponse card = paymentCardService.getDefaultCard(userPrincipal.getId());
        return ResponseEntity.ok(StandardResponse.success(card, "Default card retrieved successfully"));
    }

    /**
     * Delete a payment card
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<StandardResponse<Void>> deleteCard(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @PathVariable UUID cardId) {
        paymentCardService.deleteCard(userPrincipal.getId(), cardId);
        return ResponseEntity.ok(StandardResponse.success(null, "Card deleted successfully"));
    }

    /**
     * Set a card as default
     */
    @PutMapping("/{cardId}/default")
    public ResponseEntity<StandardResponse<CardResponse>> setDefaultCard(
            @CurrentUser @NotNull UserPrincipal userPrincipal,
            @PathVariable UUID cardId) {
        CardResponse response = paymentCardService.setDefaultCard(userPrincipal.getId(), cardId);
        return ResponseEntity.ok(StandardResponse.success(response, "Default card updated successfully"));
    }
}
