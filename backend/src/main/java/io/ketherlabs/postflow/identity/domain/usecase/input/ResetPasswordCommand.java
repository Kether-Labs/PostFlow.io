package io.ketherlabs.postflow.identity.domain.usecase.input;

/**
 * Commande d'entrée du {@code ResetPasswordUseCase}.
 *
 * @param token           le token HMAC reçu depuis le lien email
 * @param newPassword     le nouveau mot de passe en clair (min 8, max 72 chars)
 * @param confirmPassword confirmation du nouveau mot de passe
 */
public record ResetPasswordCommand(
        String token,
        String newPassword,
        String confirmPassword
) {
    public ResetPasswordCommand {
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("Token is required");

        if (newPassword == null || newPassword.isBlank())
            throw new IllegalArgumentException("New password is required");

        if (newPassword.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");

        if (newPassword.length() > 72)
            throw new IllegalArgumentException("Password must not exceed 72 characters");

        if (!newPassword.equals(confirmPassword))
            throw new IllegalArgumentException("Passwords do not match");
    }
}