package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}

