package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Adaptateur sortant implémentant {@link JwtTokenPort}.
 *
 * <p>Responsable de la génération et de la validation des Access Tokens
 * JWT signés en RS256. Les clés RSA sont lues depuis des fichiers PEM
 * au démarrage — aucune clé dans le code source ou .env.
 *
 * <p>Algorithme : RS256 (asymétrique).
 * La clé privée signe (serveur uniquement).
 * La clé publique vérifie (peut être partagée avec d'autres services).
 */
@Slf4j
@Component
public class JwtAdapter implements JwtTokenPort {

    private static final long ACCESS_TOKEN_TTL_SECONDS = 900L;

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    /**
     * Construit le {@code JwtAdapter} en chargeant les clés RSA
     * depuis des fichiers PEM.
     *
     * @param privateKeyResource chemin vers private.pem
     * @param publicKeyResource  chemin vers public.pem
     */
    public JwtAdapter(
            @Value("${jwt.private-key-path}") Resource privateKeyResource,
            @Value("${jwt.public-key-path}") Resource publicKeyResource) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // Lire et nettoyer le contenu PEM de la clé privée
            String privatePem = new String(privateKeyResource.getInputStream().readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] privateKeyBytes = Base64.getDecoder().decode(privatePem);
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            // Lire et nettoyer le contenu PEM de la clé publique
            String publicPem = new String(publicKeyResource.getInputStream().readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicPem);
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA keys from PEM files", e);
        }
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail().getValue())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    @Override
    public String extractJti(String accessToken) {
        return parseClaims(accessToken).getId();
    }

    @Override
    public UUID extractUserId(String accessToken) {
        return UUID.fromString(parseClaims(accessToken).getSubject());
    }

    @Override
    public boolean isValid(String accessToken) {
        try {
            parseClaims(accessToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token : {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
