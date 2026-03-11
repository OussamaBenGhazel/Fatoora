package tn.tradenet.elfatoora.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_account_id")
    private CompanyAccount companyAccount;

    /** Document identifier = invoice number (used as filename). */
    @Column(nullable = false)
    private String documentIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    /** Raw TEIF XML (before or after signing). */
    @Column(columnDefinition = "text")
    private String xmlContent;

    /** Signed XML (after signing). */
    @Column(columnDefinition = "text")
    private String signedXmlContent;

    /** Error message if status = ERROR or REJECTED. */
    private String errorMessage;

    private Instant sentAt;
    private Instant responseReceivedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public enum InvoiceStatus {
        DRAFT,
        PENDING_SIGN,
        SIGNED,
        SENDING,
        SENT,
        ACCEPTED,
        REJECTED,
        ERROR
    }
}
