package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.ArticleDepartment;

import java.util.List;

public interface ArticleDepartmentRepository extends JpaRepository<ArticleDepartment, Long> {

    List<ArticleDepartment> findByDepartmentId(Long departmentId);

    List<ArticleDepartment> findByArticleId(Long articleId);
}

