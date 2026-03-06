package tn.tradenet.elfatoora.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Company/account under a client. Holds SFTP and optional external DB config.
 */
@Entity
@Table(name = "company_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false)
    private String name;

    /** Account code used in SFTP path under Matricule Fiscale. */
    private String accountCode;

    /** SFTP subfolder (if different from accountCode). */
    private String sftpSubfolder;

    /** Optional: JDBC URL to client's DB for invoice retrieval. */
    private String datasourceUrl;
    private String datasourceUsername;
    private String datasourcePasswordEncrypted;
    /** Table name containing invoices (mapping provided later). */
    private String invoiceTableName;

    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
