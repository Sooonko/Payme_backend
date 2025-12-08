package com.itwizard.payme.service.impl;

import com.itwizard.payme.domain.PaymentCard;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.dto.request.AddCardRequest;
import com.itwizard.payme.dto.response.CardResponse;
import com.itwizard.payme.exception.BadRequestException;
import com.itwizard.payme.exception.ResourceNotFoundException;
import com.itwizard.payme.repository.PaymentCardRepository;
import com.itwizard.payme.repository.UserRepository;
import com.itwizard.payme.service.AuditService;
import com.itwizard.payme.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public CardResponse addCard(UUID userId, AddCardRequest request) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate card expiry
        validateCardExpiry(request.getExpiryMonth(), request.getExpiryYear());

        // Check if card token already exists
        if (paymentCardRepository.findByCardToken(request.getCardToken()).isPresent()) {
            throw new BadRequestException("This card has already been added");
        }

        // If this is the first card or marked as default, unset other defaults
        if (Boolean.TRUE.equals(request.getIsDefault()) ||
                paymentCardRepository.countByUserId(userId) == 0) {
            unsetDefaultCards(userId);
        }

        // Create payment card
        PaymentCard card = PaymentCard.builder()
                .user(user)
                .cardHolderName(request.getCardHolderName())
                .cardNumberLast4(request.getCardNumberLast4())
                .cardType(request.getCardType())
                .expiryMonth(request.getExpiryMonth())
                .expiryYear(request.getExpiryYear())
                .cardToken(request.getCardToken())
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()) ||
                        paymentCardRepository.countByUserId(userId) == 0)
                .isVerified(false) // Will be verified by payment gateway
                .build();

        card = paymentCardRepository.save(card);

        // Log action
        auditService.logAction(userId, "CARD_ADDED",
                "Added card: " + request.getCardType() + " •••• " + request.getCardNumberLast4(),
                null);

        return mapToResponse(card);
    }

    @Override
    public List<CardResponse> getUserCards(UUID userId) {
        List<PaymentCard> cards = paymentCardRepository
                .findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);

        return cards.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCard(UUID userId, UUID cardId) {
        // Find card
        PaymentCard card = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Verify ownership
        if (!card.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to delete this card");
        }

        // If deleting default card and user has other cards, set another as default
        if (card.getIsDefault()) {
            List<PaymentCard> otherCards = paymentCardRepository
                    .findByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
                    .stream()
                    .filter(c -> !c.getId().equals(cardId))
                    .toList();

            if (!otherCards.isEmpty()) {
                PaymentCard newDefault = otherCards.get(0);
                newDefault.setIsDefault(true);
                paymentCardRepository.save(newDefault);
            }
        }

        paymentCardRepository.delete(card);

        // Log action
        auditService.logAction(userId, "CARD_DELETED",
                "Deleted card: " + card.getCardType() + " •••• " + card.getCardNumberLast4(),
                null);
    }

    @Override
    @Transactional
    public CardResponse setDefaultCard(UUID userId, UUID cardId) {
        // Find card
        PaymentCard card = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        // Verify ownership
        if (!card.getUser().getId().equals(userId)) {
            throw new BadRequestException("You do not have permission to modify this card");
        }

        // Unset other default cards
        unsetDefaultCards(userId);

        // Set as default
        card.setIsDefault(true);
        card = paymentCardRepository.save(card);

        // Log action
        auditService.logAction(userId, "CARD_SET_DEFAULT",
                "Set default card: " + card.getCardType() + " •••• " + card.getCardNumberLast4(),
                null);

        return mapToResponse(card);
    }

    @Override
    public CardResponse getDefaultCard(UUID userId) {
        PaymentCard card = paymentCardRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No default card found"));

        return mapToResponse(card);
    }

    // Helper methods

    private void validateCardExpiry(Integer month, Integer year) {
        YearMonth expiry = YearMonth.of(year, month);
        YearMonth now = YearMonth.now();

        if (expiry.isBefore(now)) {
            throw new BadRequestException("Card has expired");
        }
    }

    private void unsetDefaultCards(UUID userId) {
        paymentCardRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(defaultCard -> {
                    defaultCard.setIsDefault(false);
                    paymentCardRepository.save(defaultCard);
                });
    }

    private CardResponse mapToResponse(PaymentCard card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardHolderName(card.getCardHolderName())
                .cardNumberLast4(card.getCardNumberLast4())
                .cardType(card.getCardType())
                .expiryMonth(card.getExpiryMonth())
                .expiryYear(card.getExpiryYear())
                .isDefault(card.getIsDefault())
                .isVerified(card.getIsVerified())
                .createdAt(card.getCreatedAt())
                .build();
    }
}
