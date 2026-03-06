package tn.tradenet.elfatoora.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.tradenet.elfatoora.domain.Client;
import tn.tradenet.elfatoora.domain.CompanyAccount;
import tn.tradenet.elfatoora.repository.ClientRepository;
import tn.tradenet.elfatoora.repository.CompanyAccountRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientRepository clientRepository;
    private final CompanyAccountRepository companyAccountRepository;

    @GetMapping
    public List<ClientDto> list() {
        return clientRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> get(@PathVariable Long id) {
        return clientRepository.findById(id)
            .map(c -> ResponseEntity.ok(toDto(c)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/accounts")
    public List<CompanyAccountDto> listAccounts(@PathVariable Long id) {
        return companyAccountRepository.findByClientIdAndActiveTrue(id).stream()
            .map(this::toAccountDto)
            .collect(Collectors.toList());
    }

    private ClientDto toDto(Client c) {
        ClientDto dto = new ClientDto();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setMatriculeFiscale(c.getMatriculeFiscale());
        dto.setMaxInvoicesPerDay(c.getMaxInvoicesPerDay());
        return dto;
    }

    private CompanyAccountDto toAccountDto(CompanyAccount a) {
        CompanyAccountDto dto = new CompanyAccountDto();
        dto.setId(a.getId());
        dto.setClientId(a.getClient().getId());
        dto.setName(a.getName());
        dto.setAccountCode(a.getAccountCode());
        return dto;
    }

    @lombok.Data
    public static class ClientDto {
        private Long id;
        private String name;
        private String matriculeFiscale;
        private Integer maxInvoicesPerDay;
    }

    @lombok.Data
    public static class CompanyAccountDto {
        private Long id;
        private Long clientId;
        private String name;
        private String accountCode;
    }
}
