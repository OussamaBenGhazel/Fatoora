package tn.tradenet.elfatoora.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.tradenet.elfatoora.domain.Invoice;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Page<Invoice> findByCompanyAccountId(Long companyAccountId, Pageable pageable);

    List<Invoice> findByCompanyAccountIdAndStatus(Long companyAccountId, Invoice.InvoiceStatus status);

    Optional<Invoice> findByCompanyAccountIdAndDocumentIdentifier(Long companyAccountId, String documentIdentifier);

    long countByCompanyAccount_ClientIdAndCreatedAtBetween(Long clientId, Instant start, Instant end);
}
