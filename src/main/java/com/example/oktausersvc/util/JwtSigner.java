package com.example.oktausersvc.util;

import io.jsonwebtoken.Jwts;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/** Builds private_key_jwt client assertion (RS256) as per Okta docs. */
public class JwtSigner {

    private final String clientId;
    private final String audience;
    private final String kid;
    private final PrivateKey privateKey;

    public JwtSigner(String clientId, String audience, String kid, String pemPath) {
        this.clientId = clientId;
        this.audience = audience;
        this.kid = kid;
        this.privateKey = loadPkcs8RsaPrivateKey(pemPath);
    }

    public String buildClientAssertion() {
        Instant now = Instant.now();
        return Jwts.builder()
                .header().add("kid", kid).and()
                .audience().add(audience).and()
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                .issuer(clientId)
                .subject(clientId)
                .id(UUID.randomUUID().toString())
                .signWith(privateKey /* RS256 inferred */)
                .compact();
    }

    private static PrivateKey loadPkcs8RsaPrivateKey(String pemPath) {
        try {
            String pem = Files.readString(Path.of(pemPath))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = Base64.getDecoder().decode(pem);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load PKCS#8 RSA private key from " + pemPath, e);
        }
    }
}
