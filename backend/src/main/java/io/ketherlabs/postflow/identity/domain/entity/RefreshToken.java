package io.ketherlabs.postflow.identity.domain.entity;

import java.time.Instant;
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


    private final UUID id;

    /** hash securisé*/
    private final String tokenHash;
    private final Instant expriresAt;
    /** Identifiant de l'utilisateur propriétaire du token*/
    private final UUID userId;
    /** Indique si le token a été révoqué avant son expiration. */
    private boolean revoked;
    private final Instant createdAt;

    private RefreshToken(UUID id, String tokenHash, Instant expriresAt, UUID userId, boolean revoked, Instant createdAt) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.expriresAt = expriresAt;
        this.userId = userId;
        this.revoked = revoked;
        this.createdAt = createdAt;
    }


    public static RefreshToken create(String tokenHash, Instant expriresAt,
                               UUID userId) {
        return new RefreshToken(
                UUID.randomUUID(),
                tokenHash,
                expriresAt,
                userId,
                false,
                Instant.now());
    }

    public static RefreshToken reconstruct(UUID id,String tokenHash, Instant expriresAt,
                               UUID userId, boolean revoked, Instant createdAt) {
        return new RefreshToken(
                id,
                tokenHash,
                expriresAt,
                userId,
                revoked,
                createdAt);
    }

    /**
     * Marqué un Jeton comme revoquer
     */
    public void markAsRevoke() {
        if (!this.revoked)
            this.revoked = true;
    }

    public UUID getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpriresAt() {
        return expriresAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
