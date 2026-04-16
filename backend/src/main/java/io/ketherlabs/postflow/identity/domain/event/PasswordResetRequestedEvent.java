package io.ketherlabs.postflow.identity.domain.event;

import java.time.Instant;
import java.util.UUID;


/**
 * Événement domaine publié après une demande de réinitialisation
 * de mot de passe (UC-AUTH-006).
 *
 * <p>Transporté le token brut pour que le listener puisse
 * construire le lien email sans requête DB supplémentaire.
 *
 * @param userId     l'identifiant de l'utilisateur
 * @param email      l'adresse email du destinataire
 * @param resetToken le token brut à inclure dans le lien email
 * @param occurredAt horodatage de l'événement
 */
public record PasswordResetRequestedEvent(
        UUID userId,
        String email,
        String resetToken,
        Instant occurredAt
) {

    public PasswordResetRequestedEvent(UUID userId, String email, String resetToken) {
        this(userId, email, resetToken, Instant.now());
    }
}
