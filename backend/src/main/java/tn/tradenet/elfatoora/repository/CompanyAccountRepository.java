package tn.tradenet.elfatoora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.CompanyAccount;

import java.util.List;

public interface CompanyAccountRepository extends JpaRepository<CompanyAccount, Long> {

    List<CompanyAccount> findByClientIdAndActiveTrue(Long clientId);
}
