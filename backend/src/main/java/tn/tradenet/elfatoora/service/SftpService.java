package tn.tradenet.elfatoora.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.tradenet.elfatoora.domain.CompanyAccount;
import tn.tradenet.elfatoora.domain.Invoice;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * SFTP exchange with El Fatoora: upload to 'in', read responses from 'out'.
 * Path: /MatriculeFiscale/accountCode/in and .../out.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SftpService {

    /**
     * Upload signed invoice XML to partner's 'in' folder.
     * Filename = documentIdentifier (no '/' allowed).
     */
    public void uploadInvoice(CompanyAccount account, String documentIdentifier, String signedXml) {
        String filename = documentIdentifier.replace("/", "_");
        String remotePath = buildInPath(account);
        // TODO: use JSch or Spring Integration SFTP when credentials are provided
        log.info("SFTP upload would place {} at {}/{} (configure SFTP credentials)", filename, remotePath, filename);
        // placeholder: real implementation will use JSch Session, ChannelSftp, put(InputStream, remotePath + "/" + filename)
    }

    /**
     * Poll 'out' folder for response: numeroFacture.xml (valid) or numeroFacture.error (error).
     */
    public void pollResponses(CompanyAccount account, List<Invoice> sentInvoices) {
        String outPath = buildOutPath(account);
        log.info("SFTP poll would read from {} (configure SFTP credentials)", outPath);
        // TODO: list files in outPath, match by documentIdentifier, download .xml or .error and update invoice status
    }

    private String buildInPath(CompanyAccount account) {
        String root = account.getClient().getMatriculeFiscale();
        String sub = account.getSftpSubfolder() != null ? account.getSftpSubfolder() : account.getAccountCode();
        return sub != null ? root + "/" + sub + "/in" : root + "/in";
    }

    private String buildOutPath(CompanyAccount account) {
        String root = account.getClient().getMatriculeFiscale();
        String sub = account.getSftpSubfolder() != null ? account.getSftpSubfolder() : account.getAccountCode();
        return sub != null ? root + "/" + sub + "/out" : root + "/out";
    }
}
