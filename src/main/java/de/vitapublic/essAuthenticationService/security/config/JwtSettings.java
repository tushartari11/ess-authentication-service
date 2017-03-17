package de.vitapublic.essAuthenticationService.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class JwtSettings {

    @Value("${ess.services.security.key-store}")
    private String keyStoreFile;

    @Value("${ess.services.security.key-store-password}")
    private String keyStorePassword;

    @Value("${ess.services.security.key-alias-jwt-auth}")
    private String keyStoreAlias;

    @Value("${ess.services.security.role-claim-name}")
    private String roleClaimName;

    private Key signingKey;

    public Key getSigningKey() {
        return signingKey;
    }

    public String getRoleClaimName() {
        return roleClaimName;
    }

    @PostConstruct
    public void loadKey()
            throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream(keyStoreFile), keyStorePassword.toCharArray());
        this.signingKey = ks.getCertificate(keyStoreAlias).getPublicKey();
    }


}
