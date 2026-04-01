package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    /** Sauvegarder le Jeton*/
    void save(RefreshToken token);

    /** Trouver un Jeton par son Hash */
    Optional<RefreshToken> findByTokenHash(String token);

    /** Marque un Jeton comme revoquer  */
    void revoke(RefreshToken token);

    /**
     * Supprime les jetons expirés ou révoqués.
     */
    void deleteExpiredOrRevoked();
}
