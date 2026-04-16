package io.ketherlabs.postflow.identity.domain.usecase.output;


/**
 * Résultat retourné par {@code VerifyEmailUseCase}
 * après vérification email réussie.
 *
 * @param success {@code true} si le compte a été activé
 * @param message message de confirmation
 */
public record VerifyEmailResponse(
        Boolean success,
        String message
) {
}
