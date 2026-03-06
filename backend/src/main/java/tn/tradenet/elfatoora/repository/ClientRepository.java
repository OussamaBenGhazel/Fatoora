package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByMatriculeFiscale(String matriculeFiscale);
}
