package tn.tradenet.elfatoora.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "elfatoora")
public class ElFatooraProperties {

    private Sftp sftp = new Sftp();
    private Signing signing = new Signing();
    private Limits limits = new Limits();

    @Data
    public static class Sftp {
        private String host = "localhost";
        private int port = 22;
        private String username;
        private String privateKeyPath;
        private String matriculeFiscale;
    }

    @Data
    public static class Signing {
        private String keystoreType = "PKCS11";
        private String keystorePath;
        private String keystorePassword;
        private String keyAlias;
    }

    @Data
    public static class Limits {
        private int maxInvoicesPerDayPerClient = 100;
    }
}
