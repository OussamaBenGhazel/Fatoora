package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.ArticleSupplier;

import java.util.List;

public interface ArticleSupplierRepository extends JpaRepository<ArticleSupplier, Long> {

    List<ArticleSupplier> findByArticleId(Long articleId);

    List<ArticleSupplier> findBySupplierId(Long supplierId);
}

