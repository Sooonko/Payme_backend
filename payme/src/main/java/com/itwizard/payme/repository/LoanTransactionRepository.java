package com.itwizard.payme.repository;

import com.itwizard.payme.domain.Loan;
import com.itwizard.payme.domain.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, UUID> {
    List<LoanTransaction> findByLoanOrderByCreatedAtDesc(Loan loan);
}
