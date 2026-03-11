package tn.tradenet.elfatoora.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    /**
     * If true, this user represents a managing company user who can see/manage
     * its own client plus the clients it manages.
     */
    private boolean managingCompanyUser = false;

    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
