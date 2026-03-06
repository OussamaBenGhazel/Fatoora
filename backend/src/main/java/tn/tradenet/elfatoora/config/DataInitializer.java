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
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("admin"))
                .displayName("Administrator")
                .enabled(true)
                .build();
            userRepository.save(admin);
        }
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
    }
}
