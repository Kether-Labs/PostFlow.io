package io.ketherlabs.postflow.identity.domain.port;



/**
 * Port sortant définissant le contrat de blacklist des Access Tokens JWT.
 *
 * <p>Utilisé par {@code LogoutUseCase} (UC-AUTH-005) pour invalider
 * un Access Token avant son expiration naturelle. Le {@code jti} est
 * stocké dans Redis avec un TTL égal au temps résiduel du token.
 *
 * <p>Interface pure — aucune annotation, aucun import Spring ou Redis.
 * Implémentée par {@code RedisBlacklistAdapter} dans la couche infrastructure.
 */
public interface RedisBlacklistPort {

    /**
     * Blackliste un Access Token par son {@code jti}.
     *
     * <p>Stocke la clé {@code blacklist:{jti}} dans Redis avec un TTL
     * égal au temps résiduel du token. Après expiration du TTL, Redis
     * supprime automatiquement l'entrée — aucun nettoyage manuel nécessaire.
     *
     * @param jti            le JWT ID extrait du payload du token
     * @param ttlSeconds     le temps résiduel du token en secondes
     */
    void blacklist(String jti, long ttlSeconds);

    /**
     * Vérifie si un {@code jti} est blacklisté.
     *
     * <p>Appelée par {@code JwtAuthenticationFilter} à chaque requête
     * authentifiée pour rejeter les tokens révoqués.
     *
     * @param jti le JWT ID à vérifier
     * @return {@code true} si le token est blacklisté
     */
    boolean isBlacklisted(String jti);

}
