package tn.tradenet.elfatoora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.tradenet.elfatoora.domain.CompanyAccount;
import tn.tradenet.elfatoora.domain.Invoice;
import tn.tradenet.elfatoora.repository.InvoiceRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AuditService auditService;

    public Page<Invoice> findByCompanyAccount(Long companyAccountId, Pageable pageable) {
        return invoiceRepository.findByCompanyAccountId(companyAccountId, pageable);
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> findByDocumentIdentifier(Long companyAccountId, String documentIdentifier) {
        return invoiceRepository.findByCompanyAccountIdAndDocumentIdentifier(companyAccountId, documentIdentifier);
    }

    @Transactional
    public Invoice create(CompanyAccount account, String documentIdentifier, String xmlContent, String username) {
        Invoice invoice = Invoice.builder()
            .companyAccount(account)
            .documentIdentifier(documentIdentifier)
            .status(Invoice.InvoiceStatus.DRAFT)
            .xmlContent(xmlContent)
            .build();
        invoice = invoiceRepository.save(invoice);
        auditService.log("INVOICE_CREATED", "Invoice", String.valueOf(invoice.getId()), username, "documentId=" + documentIdentifier);
        return invoice;
    }

    @Transactional
    public Invoice updateStatus(Long invoiceId, Invoice.InvoiceStatus status, String errorMessage, String username) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        invoice.setStatus(status);
        invoice.setErrorMessage(errorMessage);
        if (status == Invoice.InvoiceStatus.SENT || status == Invoice.InvoiceStatus.SENDING) {
            invoice.setSentAt(Instant.now());
        }
        if (status == Invoice.InvoiceStatus.ACCEPTED || status == Invoice.InvoiceStatus.REJECTED || status == Invoice.InvoiceStatus.ERROR) {
            invoice.setResponseReceivedAt(Instant.now());
        }
        invoice = invoiceRepository.save(invoice);
        auditService.log("INVOICE_STATUS_UPDATE", "Invoice", String.valueOf(invoiceId), username, status + (errorMessage != null ? ": " + errorMessage : ""));
        return invoice;
    }

    @Transactional
    public void setSignedContent(Long invoiceId, String signedXml) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        invoice.setSignedXmlContent(signedXml);
        invoice.setStatus(Invoice.InvoiceStatus.SIGNED);
        invoiceRepository.save(invoice);
    }

    /** Check if client has not exceeded max invoices per day (parametrable). */
    public boolean canSendMoreToday(CompanyAccount account) {
        int limit = account.getClient().getMaxInvoicesPerDay() != null
            ? account.getClient().getMaxInvoicesPerDay()
            : 100;
        Instant start = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        long count = invoiceRepository.countByCompanyAccount_ClientIdAndCreatedAtBetween(
            account.getClient().getId(), start, end);
        return count < limit;
    }
}
