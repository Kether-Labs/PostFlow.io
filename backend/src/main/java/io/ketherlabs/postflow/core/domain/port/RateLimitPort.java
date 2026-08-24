package io.ketherlabs.postflow.core.domain.port;

/**
 * Port sortant définissant le contrat de rate limiting par clé.
 *
 * <p>Interface pure — aucune annotation, aucun import Spring ou Redis.
 * Implémentée par {@code RedisRateLimitAdapter} dans la couche infrastructure.
 *
 * <p>L'algorithme utilisé est la <b>fenêtre fixe</b> : un compteur Redis
 * est incrémenté à chaque appel et expire automatiquement après
 * {@code windowSeconds} secondes. Simple, efficace, adapté à une
 * protection contre le brute-force et le scraping.
 */
public interface RateLimitPort {

    /**
     * Vérifie si une requête identifiée par {@code key} est autorisée
     * dans la fenêtre de temps courante.
     *
     * <p>Incrémente le compteur associé à {@code key} à chaque appel.
     * Si le compteur dépasse {@code maxRequests}, la méthode retourne
     * {@code false} sans interrompre d'elle-même la requête — c'est
     * à l'appelant ({@code RateLimitInterceptor}) de renvoyer le 429.
     *
     * @param key           clé unique identifiant la limite (ex : {@code "rate:auth:192.168.1.1"})
     * @param maxRequests   nombre maximum de requêtes autorisées dans la fenêtre
     * @param windowSeconds durée de la fenêtre glissante en secondes
     * @return {@code true} si la requête est dans les limites, {@code false} sinon
     */
    boolean isAllowed(String key, int maxRequests, int windowSeconds);
}
