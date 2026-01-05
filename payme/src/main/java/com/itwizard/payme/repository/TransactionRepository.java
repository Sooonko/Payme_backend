package com.itwizard.payme.repository;

import com.itwizard.payme.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Get all transactions for a specific wallet (sent or received)
    @Query("SELECT t FROM Transaction t WHERE t.fromWallet.id = :walletId OR t.toWallet.id = :walletId ORDER BY t.createdAt DESC")
    List<Transaction> findByWalletId(@Param("walletId") UUID walletId);

    // Get recent transactions (last 10)
    @Query("SELECT t FROM Transaction t WHERE t.fromWallet.id = :walletId OR t.toWallet.id = :walletId ORDER BY t.createdAt DESC")
    List<Transaction> findTop10ByWalletIdOrderByCreatedAtDesc(@Param("walletId") UUID walletId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromWallet.user.id = :userId AND t.createdAt >= :startDate AND t.type = 'SEND' AND t.status = 'COMPLETED'")
    BigDecimal calculateDailyTotal(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE (t.fromWallet.user.id = :userId OR t.toWallet.user.id = :userId) AND t.createdAt >= :since AND t.status = 'COMPLETED'")
    BigDecimal calculateTotalVolume(@Param("userId") UUID userId, @Param("since") LocalDateTime since);
}
