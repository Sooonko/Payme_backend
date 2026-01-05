package com.itwizard.payme.repository;

import com.itwizard.payme.domain.Loan;
import com.itwizard.payme.domain.RepaymentSchedule;
import com.itwizard.payme.domain.enums.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, UUID> {
    List<RepaymentSchedule> findByLoanOrderByDueDateAsc(Loan loan);

    List<RepaymentSchedule> findByLoanAndStatusOrderByDueDateAsc(Loan loan, RepaymentStatus status);
}
