package com.itwizard.payme.repository;

import com.itwizard.payme.domain.ExternalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExternalTransactionRepository extends JpaRepository<ExternalTransaction, Long> {
    List<ExternalTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);

    Optional<ExternalTransaction> findByExternalRefId(String externalRefId);
}
