package tn.tradenet.elfatoora.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Tax ID = Matricule Fiscale (used in SFTP path) */
    @Column(nullable = false, unique = true)
    private String matriculeFiscale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ClientCategory category;

    /**
     * Optional link to a managing company client. If set, this client is
     * considered a sub-client of the managing company.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "managing_company_id")
    private Client managingCompany;

    /** Max invoices per day (parametrable). */
    private Integer maxInvoicesPerDay = 100;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CompanyAccount> accounts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
