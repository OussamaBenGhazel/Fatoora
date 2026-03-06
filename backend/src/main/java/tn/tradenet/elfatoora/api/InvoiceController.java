package tn.tradenet.elfatoora.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.Invoice;
import tn.tradenet.elfatoora.service.InvoiceService;
import tn.tradenet.elfatoora.service.SigningService;
import tn.tradenet.elfatoora.service.SftpService;
import tn.tradenet.elfatoora.repository.CompanyAccountRepository;

import java.util.Map;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final SigningService signingService;
    private final SftpService sftpService;
    private final CompanyAccountRepository companyAccountRepository;

    @GetMapping
    public Page<InvoiceDto> list(
        @RequestParam Long companyAccountId,
        Pageable pageable,
        @AuthenticationPrincipal UserDetails user
    ) {
        Page<Invoice> page = invoiceService.findByCompanyAccount(companyAccountId, pageable);
        return page.map(this::toDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDto> get(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return invoiceService.findById(id)
            .map(inv -> ResponseEntity.ok(toDto(inv)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InvoiceDto> create(
        @RequestBody CreateInvoiceRequest request,
        @AuthenticationPrincipal UserDetails user
    ) {
        var account = companyAccountRepository.findById(request.getCompanyAccountId()).orElseThrow();
        if (!invoiceService.canSendMoreToday(account)) {
            return ResponseEntity.badRequest().build(); // or 429 + message
        }
        Invoice inv = invoiceService.create(
            account,
            request.getDocumentIdentifier(),
            request.getXmlContent(),
            user.getUsername()
        );
        return ResponseEntity.ok(toDto(inv));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Map<String, String>> send(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        Invoice inv = invoiceService.findById(id).orElseThrow();
        var account = inv.getCompanyAccount();
        if (!invoiceService.canSendMoreToday(account)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Daily invoice limit reached"));
        }
        try {
            invoiceService.updateStatus(id, Invoice.InvoiceStatus.SENDING, null, user.getUsername());
            String signed = signingService.sign(inv.getXmlContent());
            invoiceService.setSignedContent(id, signed);
            sftpService.uploadInvoice(account, inv.getDocumentIdentifier(), signed);
            invoiceService.updateStatus(id, Invoice.InvoiceStatus.SENT, null, user.getUsername());
            return ResponseEntity.ok(Map.of("status", "SENT"));
        } catch (Exception e) {
            invoiceService.updateStatus(id, Invoice.InvoiceStatus.ERROR, e.getMessage(), user.getUsername());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private InvoiceDto toDto(Invoice inv) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(inv.getId());
        dto.setCompanyAccountId(inv.getCompanyAccount().getId());
        dto.setDocumentIdentifier(inv.getDocumentIdentifier());
        dto.setStatus(inv.getStatus().name());
        dto.setErrorMessage(inv.getErrorMessage());
        dto.setSentAt(inv.getSentAt());
        dto.setResponseReceivedAt(inv.getResponseReceivedAt());
        dto.setCreatedAt(inv.getCreatedAt());
        return dto;
    }

    @lombok.Data
    public static class InvoiceDto {
        private Long id;
        private Long companyAccountId;
        private String documentIdentifier;
        private String status;
        private String errorMessage;
        private java.time.Instant sentAt;
        private java.time.Instant responseReceivedAt;
        private java.time.Instant createdAt;
    }

    @lombok.Data
    public static class CreateInvoiceRequest {
        private Long companyAccountId;
        private String documentIdentifier;
        private String xmlContent;
    }
}
