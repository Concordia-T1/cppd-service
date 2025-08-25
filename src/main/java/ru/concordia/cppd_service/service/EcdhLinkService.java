package ru.concordia.cppd_service.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.KDFParameters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.concordia.cppd_service.service.exceptions.EcdhContextExpiredException;
import ru.concordia.cppd_service.service.exceptions.EcdhSignatureException;
import ru.concordia.cppd_service.service.props.EcdhLinkProperties;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EcdhLinkService {
    private static final String PREFIX = "ecdh-sig:";

    private final EcdhLinkProperties ecdhLinkProperties;
    private final RedisTemplate<String, String> redisTemplate;

    private ECPublicKey publicKey;
    private ECPrivateKey privateKey;

    @PostConstruct
    public void initKeys() throws IOException {
        this.publicKey = ecdhLinkProperties.publicKey();
        this.privateKey = ecdhLinkProperties.privateKey();
    }

    @SneakyThrows
    public String issue(UUID claimId, LocalDateTime now, LocalDateTime expiry) {
        final var rng = new SecureRandom();

        final var kpg = KeyPairGenerator.getInstance("EC");
        final var ecSpec = new ECGenParameterSpec("secp256r1");
        kpg.initialize(ecSpec);

        final var ephemeralKeyPair = kpg.generateKeyPair();
        final var ephemeralPrivateKey = (ECPrivateKey) ephemeralKeyPair.getPrivate();
        final var ephemeralPublicKey = (ECPublicKey) ephemeralKeyPair.getPublic();

        final var epkSerialized = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(ephemeralPublicKey.getEncoded());

        log.info("epk: {}\n{}", epkSerialized, ephemeralPublicKey);

        final var keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(ephemeralPrivateKey);
        keyAgreement.doPhase(this.publicKey, true);

        final var sharedSecret = keyAgreement.generateSecret();

        // *

        byte[] algorithmID = "A256KW".getBytes(StandardCharsets.UTF_8);
        byte[] partyUInfo = "auth-service".getBytes(StandardCharsets.UTF_8);
        byte[] partyVInfo = "auth-service".getBytes(StandardCharsets.UTF_8);

        final var otherInfo = new byte[algorithmID.length + partyUInfo.length + partyVInfo.length];
        System.arraycopy(algorithmID, 0, otherInfo, 0, algorithmID.length);
        System.arraycopy(partyUInfo, 0, otherInfo, algorithmID.length, partyUInfo.length);
        System.arraycopy(partyVInfo, 0, otherInfo, algorithmID.length + partyUInfo.length, partyVInfo.length);

        final var params = new KDFParameters(sharedSecret, otherInfo);
        final var digest = new SHA256Digest();

        final var kdf = new ConcatenationKDFGenerator(digest);
        kdf.init(params);

        byte[] derivedKek = new byte[32];
        kdf.generateBytes(derivedKek, 0, derivedKek.length);

        byte[] derivedCek = new byte[32];
        rng.nextBytes(derivedCek);

        final var kek = new SecretKeySpec(derivedKek, "AES");
        final var cek = new SecretKeySpec(derivedCek, "AES");

        final var cipherWrap = Cipher.getInstance("AESWrap_256");
        cipherWrap.init(Cipher.WRAP_MODE, kek);
        final var cekWrapped = cipherWrap.wrap(cek);

        final var cekWrappedSerialized = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(cekWrapped);

        // *

        byte[] iv = new byte[12];
        rng.nextBytes(iv);

        final var ivSerialized = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(iv);

        final var cipherGcm = Cipher.getInstance("AES_256/GCM/NoPadding");
        final var gcmSpec = new GCMParameterSpec(128, iv);
        cipherGcm.init(Cipher.ENCRYPT_MODE, cek, gcmSpec);

        final var claimsMap = new HashMap<String, String>();
        claimsMap.put("cid", String.valueOf(claimId));
        claimsMap.put("iat", String.valueOf(now.toEpochSecond(ZoneOffset.UTC)));
        claimsMap.put("exp", String.valueOf(expiry.toEpochSecond(ZoneOffset.UTC)));

        log.info("claimsMap: {}", claimsMap);

        byte[] claims = claimsMap.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","))
                .getBytes(StandardCharsets.UTF_8);

        byte[] claimsEnc = cipherGcm.doFinal(claims);

        final var claimsEncSerialized = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(claimsEnc);

        final var ctx = String.join(".",
                new String[]{cekWrappedSerialized, ivSerialized, claimsEncSerialized});

        log.info("ctx: {}", ctx);

        final var mac = Mac.getInstance("HmacSHA256");
        mac.init(this.ecdhLinkProperties.hmacKey());

        final var sig = mac.doFinal(ctx.getBytes(StandardCharsets.UTF_8));
        final var sigSerialized = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(sig);

        storeEcdhSig(claimId, sigSerialized);
        log.info("sig: {}", sigSerialized);

        return String.format("/invite?epk=%s&ctx=%s&sig=%s", epkSerialized, ctx, sigSerialized);
    }

    @SneakyThrows
    public Map<String, String> validate(
            String epkSerialized,
            String ctxSerialized,
            String sigSerialized
    ) {
        final var mac = Mac.getInstance("HmacSHA256");
        mac.init(this.ecdhLinkProperties.hmacKey());

        byte[] sig = mac.doFinal(ctxSerialized.getBytes(StandardCharsets.UTF_8));
        byte[] expectedSig = Base64.getUrlDecoder().decode(sigSerialized);

        if (!MessageDigest.isEqual(sig, expectedSig)) {
            throw new EcdhSignatureException();
        }

        final var ctxParts = ctxSerialized.split("\\.");

        byte[] derivedEpk = Base64.getUrlDecoder().decode(epkSerialized);
        final var keySpec = new X509EncodedKeySpec(derivedEpk);

        final var kf = KeyFactory.getInstance("EC");
        final var epk = (ECPublicKey) kf.generatePublic(keySpec);

        log.info("epk:\n  {}", epk);

        final var keyAgreement = KeyAgreement.getInstance("ECDH");
        keyAgreement.init(this.privateKey);
        keyAgreement.doPhase(epk, true);

        final var sharedSecret = keyAgreement.generateSecret();

        // *

        byte[] algorithmID = "A256KW".getBytes(StandardCharsets.UTF_8);
        byte[] partyUInfo = "auth-service".getBytes(StandardCharsets.UTF_8);
        byte[] partyVInfo = "auth-service".getBytes(StandardCharsets.UTF_8);

        final var otherInfo = new byte[algorithmID.length + partyUInfo.length + partyVInfo.length];
        System.arraycopy(algorithmID, 0, otherInfo, 0, algorithmID.length);
        System.arraycopy(partyUInfo, 0, otherInfo, algorithmID.length, partyUInfo.length);
        System.arraycopy(partyVInfo, 0, otherInfo, algorithmID.length + partyUInfo.length, partyVInfo.length);

        final var params = new KDFParameters(sharedSecret, otherInfo);
        final var digest = new SHA256Digest();

        final var kdf = new ConcatenationKDFGenerator(digest);
        kdf.init(params);

        byte[] derivedKek = new byte[32];
        kdf.generateBytes(derivedKek, 0, derivedKek.length);

        final var kek = new SecretKeySpec(derivedKek, "AES");
        final var cipherWrap = Cipher.getInstance("AESWrap_256");
        cipherWrap.init(Cipher.UNWRAP_MODE, kek);

        byte[] derivedCekWrapped = Base64.getUrlDecoder().decode(ctxParts[0]);
        final var cek = (SecretKey) cipherWrap.unwrap(derivedCekWrapped, "AES", Cipher.SECRET_KEY);

        // *

        byte[] iv = Base64.getUrlDecoder().decode(ctxParts[1]);

        final var cipherGcm = Cipher.getInstance("AES_256/GCM/NoPadding");
        final var gcmSpec = new GCMParameterSpec(128, iv);
        cipherGcm.init(Cipher.DECRYPT_MODE, cek, gcmSpec);

        byte[] derivedClaims = cipherGcm.doFinal(Base64.getUrlDecoder().decode(ctxParts[2]));
        final var claims = new String(derivedClaims, StandardCharsets.UTF_8);

        final var claimsMap = Arrays.stream(claims.split(","))
                .map(pair -> pair.trim().split(":", 2))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),
                        pair -> pair[1].trim()
                ));

        log.info("claimsMap: {}", claimsMap);

        final var cid = UUID.fromString(claimsMap.get("cid"));
        final var now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        if (Long.parseLong(claimsMap.get("exp")) <= now
                || !isActiveEcdhSig(cid, sigSerialized))
            throw new EcdhContextExpiredException();

        return claimsMap;
    }

    public void storeEcdhSig(UUID id, String sig) {
        final var key = PREFIX + id;
        redisTemplate.opsForValue().set(key, sig);
        redisTemplate.expire(key, ecdhLinkProperties.getExpiry(), TimeUnit.SECONDS);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isActiveEcdhSig(UUID id, String providedSig) {
        final var storedToken = redisTemplate.opsForValue().get(PREFIX + id);
        return storedToken != null && storedToken.equals(providedSig);
    }

    public void revokeEcdhSig(UUID id) {
        redisTemplate.delete(PREFIX + id);
    }
}
