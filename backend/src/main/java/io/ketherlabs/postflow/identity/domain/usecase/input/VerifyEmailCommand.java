package io.ketherlabs.postflow.identity.domain.usecase.input;


/**
 * Commande d'entrée du {@code VerifyEmailUseCase}.
 *
 * <p>Contient le token reçu depuis le lien email envoyé
 * lors de l'inscription (UC-AUTH-001).
 *
 * @param token la valeur du token de vérification
 */
public record VerifyEmailCommand(String token) {

    public VerifyEmailCommand {
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("Verification token is required");
    }
}
