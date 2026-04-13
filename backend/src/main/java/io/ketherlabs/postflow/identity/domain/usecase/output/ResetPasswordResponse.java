package io.ketherlabs.postflow.identity.domain.usecase.output;

/**
 * Résultat retourné par {@code ResetPasswordUseCase}
 * après réinitialisation réussie.
 *
 * @param message message de confirmation
 */
public record ResetPasswordResponse(String message) {

    public static ResetPasswordResponse success() {
        return new ResetPasswordResponse(
                "Password reset successfully. Please log in with your new password."
        );
    }
}