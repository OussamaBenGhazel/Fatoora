package tn.tradenet.elfatoora.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.tradenet.elfatoora.domain.Client;
import tn.tradenet.elfatoora.domain.CompanyAccount;
import tn.tradenet.elfatoora.domain.User;
import tn.tradenet.elfatoora.repository.ClientRepository;
import tn.tradenet.elfatoora.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Admin (no client attached)
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin"))
                .displayName("Administrator")
                .enabled(true)
                .build();
            userRepository.save(admin);
        }

        // Demo client + default account if database is empty (first run)
        if (clientRepository.count() == 0) {
            Client client = Client.builder()
                .name("Demo Client")
                .matriculeFiscale("DEMO001")
                .maxInvoicesPerDay(100)
                .build();
            client = clientRepository.save(client);
            CompanyAccount account = CompanyAccount.builder()
                .client(client)
                .name("Default Account")
                .accountCode("default")
                .active(true)
                .build();
            client.getAccounts().add(account);
            clientRepository.save(client);
        }

        // Managing company client (MANA001) and a sub-client (SUB001) for testing roles
        Client managingCompany = clientRepository.findByMatriculeFiscale("MANA001")
            .orElseGet(() -> {
                Client c = Client.builder()
                    .name("Managing Company A")
                    .matriculeFiscale("MANA001")
                    .maxInvoicesPerDay(200)
                    .build();
                c = clientRepository.save(c);
                CompanyAccount acc = CompanyAccount.builder()
                    .client(c)
                    .name("Managing A Main")
                    .accountCode("MANA_MAIN")
                    .active(true)
                    .build();
                c.getAccounts().add(acc);
                return clientRepository.save(c);
            });

        Client soloClient = clientRepository.findByMatriculeFiscale("SUB001")
            .orElseGet(() -> {
                Client c = Client.builder()
                    .name("Solo Client 1")
                    .matriculeFiscale("SUB001")
                    .maxInvoicesPerDay(100)
                    .managingCompany(managingCompany)
                    .build();
                c = clientRepository.save(c);
                CompanyAccount acc = CompanyAccount.builder()
                    .client(c)
                    .name("Solo1 Account")
                    .accountCode("SOLO1_ACC")
                    .active(true)
                    .build();
                c.getAccounts().add(acc);
                return clientRepository.save(c);
            });

        // Managing-company login: managerA / managerA
        if (userRepository.findByUsername("managerA").isEmpty()) {
            User manager = User.builder()
                .username("managerA")
                .passwordHash(passwordEncoder.encode("managerA"))
                .displayName("Manager A")
                .enabled(true)
                .client(managingCompany)
                .managingCompanyUser(true)
                .build();
            userRepository.save(manager);
        }

        // Solo client login: solo1 / solo1
        if (userRepository.findByUsername("solo1").isEmpty()) {
            User soloUser = User.builder()
                .username("solo1")
                .passwordHash(passwordEncoder.encode("solo1"))
                .displayName("Solo Client 1 User")
                .enabled(true)
                .client(soloClient)
                .managingCompanyUser(false)
                .build();
            userRepository.save(soloUser);
        }
    }
}
