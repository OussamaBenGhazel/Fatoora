package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByMatriculeFiscale(String matriculeFiscale);

    Optional<Client> findByMatriculeFiscale(String matriculeFiscale);

    List<Client> findByManagingCompanyIdOrId(Long managingCompanyId, Long id);
}
