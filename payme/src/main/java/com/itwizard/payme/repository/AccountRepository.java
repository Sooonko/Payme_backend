package com.itwizard.payme.repository;

import com.itwizard.payme.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUserId(UUID userId);

    Optional<Account> findByPhone(String phone);

    List<Account> findByNameContainingIgnoreCase(String name);

    boolean existsByPhone(String phone);
}
