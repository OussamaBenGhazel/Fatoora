package tn.tradenet.elfatoora.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sub_family_id")
    private ArticleSubFamily subFamily;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private String unit;

    /** Default purchase price for Gestion Achat. */
    private BigDecimal purchasePrice;

    /** Default sale price for Gestion Vente. */
    private BigDecimal salePrice;

    /** Current stock quantity for Gestion Stock (optional). */
    private BigDecimal stockQuantity;

    private boolean active = true;
}

