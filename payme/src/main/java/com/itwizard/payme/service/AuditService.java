package com.itwizard.payme.service;

import com.itwizard.payme.domain.AuditLog;
import com.itwizard.payme.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final com.itwizard.payme.repository.UserRepository userRepository;

    @Transactional
    public void logAction(UUID userId, String action, String details, String ipAddress) {
        com.itwizard.payme.domain.User user = userRepository.findById(userId).orElse(null);

        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}
