package com.itwizard.payme.repository;

import com.itwizard.payme.domain.Loan;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.domain.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserOrderByCreatedAtDesc(User user);

    List<Loan> findByUserAndStatus(User user, LoanStatus status);
}
