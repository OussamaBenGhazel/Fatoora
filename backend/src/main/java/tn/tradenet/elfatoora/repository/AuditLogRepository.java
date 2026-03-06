package tn.tradenet.elfatoora.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.AuditLog;

import java.time.Instant;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(Instant from, Instant to, Pageable pageable);

    Page<AuditLog> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);
}
