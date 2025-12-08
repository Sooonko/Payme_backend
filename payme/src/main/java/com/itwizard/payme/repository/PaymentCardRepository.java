package com.itwizard.payme.repository;

import com.itwizard.payme.domain.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID> {

    // Find all cards for a specific user
    List<PaymentCard> findByUserIdOrderByIsDefaultDescCreatedAtDesc(UUID userId);

    // Find default card for a user
    Optional<PaymentCard> findByUserIdAndIsDefaultTrue(UUID userId);

    // Find card by token
    Optional<PaymentCard> findByCardToken(String cardToken);

    // Check if user has any verified cards
    @Query("SELECT COUNT(pc) > 0 FROM PaymentCard pc WHERE pc.user.id = :userId AND pc.isVerified = true")
    boolean hasVerifiedCards(@Param("userId") UUID userId);

    // Count cards for a user
    long countByUserId(UUID userId);
}
