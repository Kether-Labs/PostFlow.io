package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
 * JWT signés en RS256. Les clés RSA sont lues depuis les variables
 * d'environnement au démarrage — aucune clé dans le code source.
 *
 * <p>Algorithme : RS256 (asymétrique).
 * La clé privée signe (serveur uniquement).
 * La clé publique vérifie (peut être partagée avec d'autres services).
 *
 * <p>Structure du payload JWT :
 * <pre>
 * {
 *   "sub":   "userId (UUID)",
 *   "email": "user@example.com",
 *   "iat":   1711101600,
 *   "exp":   1711102500,
 *   "jti":   "UUID unique — utilisé pour la blacklist Redis"
 * }
 * </pre>
 */
@Slf4j
@Component
public class JwtAdapter implements JwtTokenPort {


    private static final long ACCESS_TOKEN_TTL_SECONDS = 900L;

    private final PrivateKey privateKey;
    private final PublicKey  publicKey;

    /**
     * Construit le {@code JwtAdapter} en chargeant les clés RSA
     * depuis les variables d'environnement au démarrage.
     *
     * <p>Le démarrage échoue immédiatement si les clés sont absentes
     * ou malformées (fail-fast).
     *
     * @param privateKeyBase64 clé privée RSA encodée en Base64 (PKCS8)
     * @param publicKeyBase64  clé publique RSA encodée en Base64 (X509)
     */
    public JwtAdapter(
            @Value("${jwt.private-key}") String privateKeyBase64,
            @Value("${jwt.public-key}")  String publicKeyBase64) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            byte[] privateKeyBytes = Base64.getDecoder().decode(
                    privateKeyBase64.replaceAll("\\s+", "")
            );
            this.privateKey = keyFactory.generatePrivate(
                    new PKCS8EncodedKeySpec(privateKeyBytes)
            );

            byte[] publicKeyBytes = Base64.getDecoder().decode(
                    publicKeyBase64.replaceAll("\\s+", "")
            );
            this.publicKey = keyFactory.generatePublic(
                    new X509EncodedKeySpec(publicKeyBytes)
            );

        } catch (Exception e) {
            // Fail-fast : le service ne doit pas démarrer sans clés valides
            throw new IllegalStateException(
                    "Failed to load RSA keys for JWT — check jwt.private-key " +
                            "and jwt.public-key in application.yml", e
            );
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Génère un JWT avec les claims : {@code sub}, {@code email},
     * {@code iat}, {@code exp} (now + 15min), {@code jti} (UUID unique).
     */
    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail().getValue())
                .id(UUID.randomUUID().toString())       // jti unique → blacklist Redis
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS)))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /**
     * {@inheritDoc}
     *
     * @throws io.jsonwebtoken.JwtException si le token est invalide ou expiré
     */
    @Override
    public String extractJti(String accessToken) {
        return parseClaims(accessToken).getId();
    }

    /**
     * {@inheritDoc}
     *
     * @throws io.jsonwebtoken.JwtException si le token est invalide ou expiré
     */
    @Override
    public UUID extractUserId(String accessToken) {
        return UUID.fromString(parseClaims(accessToken).getSubject());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Retourne {@code false} sans lever d'exception si le token
     * est invalide, expiré, ou malformé — le filtre JWT gère le cas.
     */
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


    /**
     * Parse et valide un JWT en vérifiant la signature RS256 et l'expiration.
     *
     * @param token le JWT à parser
     * @return les claims extraits du payload
     * @throws JwtException si la signature est invalide ou le token expiré
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}