package io.ketherlabs.postflow.identity.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Entité du domaine Identity représentant un jeton de rafraîchissement (Refresh Token).
 *
 * <p>Un {@code RefreshToken} est émis lors de l'authentification d'un utilisateur
 * et permet de renouveler un jeton d'accès (access token) sans nécessiter
 * une nouvelle authentification complète.</p>
 *
 * <p>Cette classe est une entité DDD pure, sans dépendance Spring/JPA.</p>
 *
 */
public class RefreshToken {


    /** hash securisé*/
    private final String tokenHash;
    private final LocalDateTime expriresAt;
    /** Identifiant de l'utilisateur propriétaire du token*/
    private final UUID userId;
    /** Indique si le token a été révoqué avant son expiration. */
    private boolean revoked;
    private final LocalDate createdAt;

    private RefreshToken(String tokenHash, LocalDateTime expriresAt, UUID userId, boolean revoked, LocalDate createdAt) {
        this.tokenHash = tokenHash;
        this.expriresAt = expriresAt;
        this.userId = userId;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }


    public static RefreshToken create(String tokenHash, LocalDateTime expriresAt,
                               UUID userId) {
        return new RefreshToken(
                tokenHash,
                expriresAt,
                userId,
                false,
                LocalDate.now()
        );
    }

    public static RefreshToken reconstruct(String tokenHash, LocalDateTime expriresAt,
                               UUID userId, boolean revoked, LocalDate createdAt) {
        return new RefreshToken(
                tokenHash,
                expriresAt,
                userId,
                revoked,
                createdAt
        );
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public LocalDateTime getExpriresAt() {
        return expriresAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
}
