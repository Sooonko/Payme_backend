package com.itwizard.payme.repository;

import com.itwizard.payme.domain.AuditLog;
import com.itwizard.payme.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserOrderByTimestampDesc(User user);
}
