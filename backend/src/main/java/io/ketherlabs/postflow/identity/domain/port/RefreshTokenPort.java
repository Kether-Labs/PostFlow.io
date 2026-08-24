package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.User;

/**
 * Port pour générer et valider les Refresh Tokens.
 */
public interface RefreshTokenPort {

    /**
     * Génère un nouveau Refresh Token opaque (UUID).
     *
     * @param user l'utilisateur pour lequel générer le token
     * @return le token (UUID) à retourner au client
     */
    String generateRefreshToken(User user);

    /**
     * Vérifie si un Refresh Token est valide.
     *
     * @param tokenHash le hash du token stocké en base
     * @return true si le token est valide et non expiré
     */
    boolean isValid(String tokenHash);
}