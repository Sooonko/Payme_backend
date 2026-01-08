package com.itwizard.payme.repository;

import com.itwizard.payme.domain.LoanEligibility;
import com.itwizard.payme.domain.LoanProduct;
import com.itwizard.payme.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanEligibilityRepository extends JpaRepository<LoanEligibility, UUID> {
    List<LoanEligibility> findByUserId(UUID userId);

    Optional<LoanEligibility> findByUserAndProduct(User user, LoanProduct product);
}
