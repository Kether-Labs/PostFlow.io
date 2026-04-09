package io.ketherlabs.postflow.identity.domain.entity;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité du domaine Identity représentant un jeton de réinitialisation de mot de passe.
 *
 * <p>Un {@code PasswordResetToken} est généré lors d'une demande de réinitialisation
 * de mot de passe (événement {@code PasswordResetRequestedEvent}) et permet de
 * valider l'identité de l'utilisateur avant de procéder au changement.</p>
 *
 * <p>Ce token est à usage unique : il est marqué comme utilisé ({@code used = true})
 * dès sa consommation, et expire après une durée définie.</p>
 *
 * <p>Cette classe est une entité DDD pure, sans dépendance Spring/JPA.</p>
 *
 */
public class PasswordResetToken {

    private final UUID id;

    /** Hash sécurisé du token brut. */
    private final String tokenHash;

    /** Identifiant de l'utilisateur ayant initié la demande de réinitialisation. */
    private final UUID userId;

    /** Date et heure d'expiration du token. */
    private final Instant expiresAt;

    /** Indique si le token a déjà été utilisé. */
    private boolean used;

    /** Date et heure de création du token. */
    private final LocalDateTime createdAt;

    private PasswordResetToken(UUID id, String tokenHash, UUID userId, Instant expiresAt, LocalDateTime createdAt,
                               boolean used) {
        this.id = id;
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.used = used;
    }


    public static PasswordResetToken create(String tokenHash, UUID userId,
                                            Instant expiresAt) {
        return new PasswordResetToken(
                UUID.randomUUID(),
                tokenHash,
                userId,
                expiresAt,
                LocalDateTime.now(),
                false
        );
    }

    public static PasswordResetToken reconstruct(UUID id, String tokenHash, UUID userId,
                                                 Instant expiresAt, LocalDateTime createdAt, boolean used) {
        return new PasswordResetToken(
                id,
                tokenHash,
                userId,
                expiresAt,
                createdAt,
                used
        );
    }


    public void markAsUsed() {
        if (!this.used)
            this.used = true;
    }

    public UUID getId() {
        return id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
