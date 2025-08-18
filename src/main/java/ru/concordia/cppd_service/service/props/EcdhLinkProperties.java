package ru.concordia.cppd_service.service.props;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "ecdh")
public class EcdhLinkProperties {
    private Long expiry;

    private Resource hmacKeyRes;
    private Resource privateKeyRes;
    private Resource publicKeyRes;

    public SecretKey hmacKey() throws IOException {
        try (InputStream stream = hmacKeyRes.getInputStream()) {
            final var inner = stream.readAllBytes();
            return new SecretKeySpec(Base64.getDecoder().decode(inner), "HmacSHA256");
        }
    }

    public ECPrivateKey privateKey() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(privateKeyRes.getInputStream())) {
            final var pemParser = new PEMParser(reader);
            final var converter = new JcaPEMKeyConverter();

            final var keyPair = (PEMKeyPair) pemParser.readObject();
            return (ECPrivateKey) converter.getPrivateKey(keyPair.getPrivateKeyInfo());
        }
    }

    public ECPublicKey publicKey() throws IOException {
        try (InputStreamReader reader = new InputStreamReader(publicKeyRes.getInputStream())) {
            final var pemParser = new PEMParser(reader);
            final var converter = new JcaPEMKeyConverter();

            final var publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
            return (ECPublicKey) converter.getPublicKey(publicKeyInfo);
        }
    }
}
