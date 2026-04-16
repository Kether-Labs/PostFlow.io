package io.ketherlabs.postflow.identity.domain.port;

/**
 * Port sortant définissant le contrat de hashage et vérification
 * des mots de passe.
 *
 * <p>Interface pure — aucune dépendance Spring ou BCrypt.
 * Implémentée par {@code BCryptPasswordEncoderAdapter}
 * dans la couche infrastructure.
 */
public interface PasswordEncoderPort {

    /**
     * Hashe un mot de passe en clair avec bcrypt cost=12.
     *
     * <p>Utilisée par {@code RegisterUseCase} et {@code ResetPasswordUseCase}.
     *
     * @param rawPassword le mot de passe en clair
     * @return le hash bcrypt
     */
    String encode(String rawPassword);

    /**
     * Vérifie qu'un mot de passe en clair correspond à un hash bcrypt.
     *
     * <p>Utilisée par {@code LoginUseCase} (UC-AUTH-003).
     *
     * @param rawPassword    le mot de passe en clair saisi
     * @param hashedPassword le hash bcrypt stocké en base
     * @return {@code true} si le mot de passe correspond au hash
     */
    boolean matches(String rawPassword, String hashedPassword);
}