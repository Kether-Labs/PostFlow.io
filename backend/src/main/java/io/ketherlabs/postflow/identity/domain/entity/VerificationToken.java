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
 * @see UserRegisteredEvent
 */
public class VerificationToken {

    /** Valeur brute du token de vérification (ex. UUID ou token signé). */
    private final String token;

    /** Identifiant de l'utilisateur dont l'email doit être vérifié. */
    private final UUID userid;

    /** Date et heure d'expiration du token. */
    private final Instant expriredAt;

    /** Indique si le token a déjà été utilisé pour la vérification. */
    private boolean used;


    private VerificationToken(String token, UUID userid, Instant expriredAt, boolean used) {
        this.token = token;
        this.userid = userid;
        this.expriredAt = expriredAt;
        this.used = used;
    }


    public static VerificationToken create(String token, UUID userid, Instant expriredAt) {
        return  new VerificationToken(token, userid, expriredAt, false);
    }

    /**
     * Permet d'affecter un token comme étant utilisé
     */
    public void markAsUsed() {
        if (!this.used)
            this.used = true;
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

    public boolean isUsed() {
        return used;
    }
}
