package tn.tradenet.elfatoora.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.AuditLog;
import tn.tradenet.elfatoora.service.AuditService;

import java.time.Instant;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public Page<AuditLogDto> list(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
        @RequestParam(required = false) String username,
        Pageable pageable,
        @AuthenticationPrincipal UserDetails user
    ) {
        if (username != null && !username.isBlank()) {
            return auditService.findByUser(username, pageable).map(this::toDto);
        }
        if (from != null && to != null) {
            return auditService.findBetween(from, to, pageable).map(this::toDto);
        }
        Instant end = Instant.now();
        Instant start = end.minusSeconds(7 * 24 * 3600); // last 7 days
        return auditService.findBetween(start, end, pageable).map(this::toDto);
    }

    private AuditLogDto toDto(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setAction(log.getAction());
        dto.setEntityType(log.getEntityType());
        dto.setEntityId(log.getEntityId());
        dto.setUsername(log.getUsername());
        dto.setDetails(log.getDetails());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    @lombok.Data
    public static class AuditLogDto {
        private Long id;
        private String action;
        private String entityType;
        private String entityId;
        private String username;
        private String details;
        private Instant createdAt;
    }
}
