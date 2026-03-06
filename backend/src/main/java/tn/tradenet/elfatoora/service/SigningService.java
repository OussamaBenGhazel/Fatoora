package tn.tradenet.elfatoora.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.tradenet.elfatoora.config.ElFatooraProperties;

/**
 * XAdES-B signing for El Fatoora (supplier signature).
 * Uses EU DSS when keystore is configured; otherwise throws or returns placeholder for dev.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SigningService {

    private final ElFatooraProperties properties;

    /**
     * Sign the invoice XML with XAdES-B (SigFrs, RSA-SHA256, etc.).
     * Requires ANCE certificate (token) to be configured.
     */
    public String sign(String invoiceXml) {
        if (invoiceXml == null || invoiceXml.isBlank()) {
            throw new IllegalArgumentException("Invoice XML is required");
        }
        String keystorePath = properties.getSigning().getKeystorePath();
        if (keystorePath == null || keystorePath.isBlank()) {
            log.warn("Signing not configured (no keystore). Returning XML unchanged - configure token for production.");
            return invoiceXml;
        }
        try {
            return signWithDss(invoiceXml);
        } catch (Exception e) {
            log.error("Signing failed", e);
            throw new RuntimeException("Signing failed: " + e.getMessage());
        }
    }

    /**
     * DSS-based XAdES-B signing. To be implemented with DSS API once token is available.
     * Spec: Id="SigFrs", RSA-SHA256, exclusive c14n, SHA256 digest.
     */
    private String signWithDss(String invoiceXml) {
        // TODO: integrate eu.europa.ec.joinup.sd-dss when token is configured
        // - Load PKCS11 keystore with certificate from token
        // - Build XAdES-B signature with SignedProperties (SigningTime, SigningCertificateV2, ClaimedRole "Fournisseur", SigPolicyId, etc.)
        // - Insert signature into document and return
        throw new UnsupportedOperationException(
            "DSS signing not yet implemented. Configure keystore (token) and add DSS integration.");
    }
}
