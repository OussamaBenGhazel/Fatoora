package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.ClientCategory;

public interface ClientCategoryRepository extends JpaRepository<ClientCategory, Long> {
}

