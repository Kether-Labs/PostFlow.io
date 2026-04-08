package io.ketherlabs.postflow.identity.domain.entity;

import java.time.Instant;
import java.util.UUID;


/**
 * Entité du domaine Identity représentant un jeton de vérification d'adresse email.
 *
 * <p>Un {@code VerificationToken} est généré lors de l'inscription d'un utilisateur
 * (événement {@code UserRegisteredEvent}) et permet de confirmer la validité
 * de son adresse email avant l'activation du compte.</p>
 *
 * <p>Ce token est à usage unique : il est marqué comme utilisé ({@code used = true})
 * dès sa consommation, et expire après une durée définie.</p>
 *
 * <p>Cette classe est une entité DDD pure, sans dépendance Spring/JPA.</p>
 *
 */
public class VerificationToken {

    private final UUID id;

    /**
     * Valeur brute du token de vérification (ex. UUID ou token signé).
     */
    private final String token;

    /**
     * Identifiant de l'utilisateur dont l'email doit être vérifié.
     */
    private final UUID userid;

    /**
     * Date et heure d'expiration du token.
     */
    private final Instant expriredAt;

    private final Instant createdAt;

    /**
     * Indique si le token a déjà été utilisé pour la vérification.
     */
    private boolean used;


    private VerificationToken(UUID id, String token, UUID userid, Instant expriredAt, Instant createdAt, boolean used) {
        this.id = id;
        this.token = token;
        this.userid = userid;
        this.expriredAt = expriredAt;
        this.createdAt = createdAt;
        this.used = used;
    }


    /**
     * Crée un nouveau token de vérification email (UC-AUTH-001).
     *
     * <p>Appelée par {@code RegisterUseCase} après la persistance
     * de l'utilisateur. Le token est un UUID aléatoire sécurisé.
     * TTL fixé à 24 heures conformément à la spécification API.
     *
     * @param token   la valeur du token généré (UUID ou HMAC)
     * @param userId  l'identifiant de l'utilisateur à vérifier
     * @return une nouvelle instance de {@code VerificationToken}
     */
    public static VerificationToken create(String token, UUID userId) {
        return new VerificationToken(
                UUID.randomUUID(),
                token,
                userId,
                Instant.now().plusSeconds(86400), // TTL 24h
                Instant.now(),
                false
        );
    }

    /**
     * Reconstruit un {@code VerificationToken} depuis la base de données.
     *
     * <p>Appelée uniquement par {@code VerificationTokenJpaEntity.toDomain()}.
     * Aucune génération d'UUID, aucun effet de bord.
     *
     * @param id        l'identifiant unique existant
     * @param token     la valeur du token
     * @param userId    l'identifiant de l'utilisateur associé
     * @param expiresAt la date d'expiration stockée en base
     * @param used      {@code true} si le token a déjà été consommé
     * @param createdAt la date de création
     * @return une instance reconstituée
     */
    public static VerificationToken reconstruct(UUID id, String token, UUID userId,
                                                Instant expiresAt, boolean used,
                                                Instant createdAt) {
        return new VerificationToken(id, token, userId,
                expiresAt, createdAt, used);
    }

    /**
     * Marque ce token comme utilisé après vérification email réussie.
     *
     * <p>Appelée par {@code VerifyEmailUseCase} (UC-AUTH-002) — étape 6 :
     * "UPDATE token SET used = true".
     *
     * @throws IllegalStateException si le token est déjà utilisé
     */
    public void markAsUsed() {
        if (this.used)
            throw new IllegalStateException("Token has already been used");
        this.used = true;
    }

    /**
     * Vérifie si ce token est encore valide.
     *
     * <p>Un token est valide s'il n'est pas encore utilisé
     * et que sa date d'expiration n'est pas dépassée.
     *
     * @return {@code true} si le token est utilisable
     */
    public boolean isValid() {
        return !this.used && Instant.now().isBefore(this.expriredAt);
    }


    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public UUID getUserid() {
        return userid;
    }

    public Instant getExpriredAt() {
        return expriredAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isUsed() {
        return used;
    }
}
