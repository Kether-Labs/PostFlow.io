package io.ketherlabs.postflow.identity.domain.port;


import io.ketherlabs.postflow.identity.domain.entity.User;

import java.util.UUID;

/**
 * Port sortant définissant le contrat de génération et validation
 * des Access Tokens JWT RS256.
 *
 * <p>Interface pure — aucune annotation, aucun import Spring ou JJWT.
 * Implémentée par {@code JwtAdapter} dans la couche infrastructure.
 */
public interface JwtTokenPort {

    /**
     * Génère un Access Token JWT RS256 pour un utilisateur authentifié.
     *
     * <p>Le payload contient : {@code sub} (userId), {@code email},
     * {@code iat} (now), {@code exp} (now + 15min), {@code jti} (UUID unique).
     *
     * @param user l'utilisateur pour lequel générer le token
     * @return le JWT signé en RS256 sous forme de String
     */
    String generateAccessToken(User user);

    /**
     * Extrait le {@code jti} (JWT ID) d'un Access Token.
     *
     * <p>Utilisé par {@code LogoutUseCase} (UC-AUTH-005) pour blacklister
     * le token dans Redis avec son TTL résiduel.
     *
     * @param accessToken le JWT à parser
     * @return le {@code jti} extrait du payload
     */
    String extractJti(String accessToken);

    /**
     * Extrait le {@code sub} (userId) d'un Access Token.
     *
     * @param accessToken le JWT à parser
     * @return l'UUID de l'utilisateur extrait du payload
     */
    UUID extractUserId(String accessToken);

    /**
     * Valide la signature RS256 et l'expiration d'un Access Token.
     *
     * <p>Ne vérifie PAS la blacklist Redis — cette vérification
     * est faite séparément par {@code JwtAuthenticationFilter}.
     *
     * @param accessToken le JWT à valider
     * @return {@code true} si la signature est valide et le token non expiré
     */
    boolean isValid(String accessToken);

}
