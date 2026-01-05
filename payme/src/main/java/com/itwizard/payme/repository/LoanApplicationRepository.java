package com.itwizard.payme.repository;

import com.itwizard.payme.domain.LoanApplication;
import com.itwizard.payme.domain.User;
import com.itwizard.payme.domain.enums.LoanApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    List<LoanApplication> findByUserOrderByCreatedAtDesc(User user);

    List<LoanApplication> findByUserAndStatus(User user, LoanApplicationStatus status);
}
