package tn.tradenet.elfatoora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tn.tradenet.elfatoora.domain.AuditLog;
import tn.tradenet.elfatoora.repository.AuditLogRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void log(String action, String entityType, String entityId, String username, String details) {
        AuditLog log = AuditLog.builder()
            .action(action)
            .entityType(entityType)
            .entityId(entityId)
            .username(username)
            .details(details)
            .build();
        auditLogRepository.save(log);
    }

    public Page<AuditLog> findBetween(Instant from, Instant to, Pageable pageable) {
        return auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to, pageable);
    }

    public Page<AuditLog> findByUser(String username, Pageable pageable) {
        return auditLogRepository.findByUsernameOrderByCreatedAtDesc(username, pageable);
    }
}
