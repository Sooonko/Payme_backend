package com.itwizard.payme.service;

import com.itwizard.payme.dto.request.AddCardRequest;
import com.itwizard.payme.dto.response.CardResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentCardService {

    /**
     * Add a new payment card for a user
     */
    CardResponse addCard(UUID userId, AddCardRequest request);

    /**
     * Get all payment cards for a user
     */
    List<CardResponse> getUserCards(UUID userId);

    /**
     * Delete a payment card
     */
    void deleteCard(UUID userId, UUID cardId);

    /**
     * Set a card as default
     */
    CardResponse setDefaultCard(UUID userId, UUID cardId);

    /**
     * Get default card for a user
     */
    CardResponse getDefaultCard(UUID userId);
}
