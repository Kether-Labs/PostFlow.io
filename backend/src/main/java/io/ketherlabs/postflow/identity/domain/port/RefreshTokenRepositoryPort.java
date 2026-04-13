package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    /** Sauvegarder le Jeton*/
    void save(RefreshToken token);

    /** Trouver un Jeton par son Hash */
    Optional<RefreshToken> findByTokenHash(String token);

    List<RefreshToken> findByUserId(UUID userId);

    /** Marque un Jeton comme revoquer  */
    void revoke(RefreshToken token);

    /**
     * Supprime les jetons expirés ou révoqués.
     */
    void deleteExpiredOrRevoked();

    /**
     * Révoque tous les Refresh Tokens actifs d'un utilisateur.
     *
     * <p>Appelée par {@code ResetPasswordUseCase} (UC-AUTH-007)
     * après reset du mot de passe — toutes les sessions actives
     * sont invalidées pour forcer une reconnexion.
     *
     * @param userId l'identifiant de l'utilisateur
     */
    void revokeAllByUserId(UUID userId);
}
