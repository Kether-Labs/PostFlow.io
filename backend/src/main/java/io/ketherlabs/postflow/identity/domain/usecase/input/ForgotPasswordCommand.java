package io.ketherlabs.postflow.identity.domain.usecase.input;

/**
 * Commande d'entrée du {@code ForgotPasswordUseCase}.
 *
 * @param email l'adresse email du compte concerné
 */
public record ForgotPasswordCommand(String email) {

    public ForgotPasswordCommand {
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");
    }
}