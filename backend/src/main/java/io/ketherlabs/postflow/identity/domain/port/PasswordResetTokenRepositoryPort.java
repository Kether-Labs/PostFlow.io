package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {

    /**
     * Sauvegarde un jeton de réinitialisation.
     */
    void save(PasswordResetToken token);

    /**
     * Trouve un jeton valide par son hash.
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /**
     * Marque un jeton comme utilisé.
     */
    void markAsUsed(PasswordResetToken token);

    /**
     * Supprime les jetons expirés ou utilisés.
     */
    void deleteExpiredOrUsed();
}
