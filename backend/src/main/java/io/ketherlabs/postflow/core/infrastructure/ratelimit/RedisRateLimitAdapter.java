package io.ketherlabs.postflow.core.infrastructure.ratelimit;

import io.ketherlabs.postflow.core.domain.port.RateLimitPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Adaptateur sortant implémentant {@link RateLimitPort} via Redis.
 *
 * <p>Utilise l'algorithme de <b>fenêtre fixe</b> :
 * <ol>
 *   <li>{@code INCR key} — incrémente le compteur atomiquement.</li>
 *   <li>{@code EXPIRE key windowSeconds} — positionne le TTL uniquement
 *       à la première requête ({@code count == 1}) pour que la fenêtre
 *       démarre au premier appel et s'efface seule.</li>
 * </ol>
 *
 * <p>Réutilise le {@link StringRedisTemplate} déclaré dans
 * {@code RedisConfig} — aucune connexion Redis supplémentaire.
 *
 * <p>Limite connue : entre {@code INCR} et {@code EXPIRE}, une infime
 * fenêtre de race condition existe. En pratique, elle est négligeable pour
 * du rate limiting; un script Lua serait nécessaire pour une atomicité totale.
 */
@Component
@RequiredArgsConstructor
public class RedisRateLimitAdapter implements RateLimitPort {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * {@inheritDoc}
     *
     * <p>Retourne {@code false} (requête bloquée) si Redis est indisponible
     * et que {@code increment()} renvoie {@code null} — comportement
     * conservateur : on bloque plutôt que de laisser passer sans contrôle.
     */
    @Override
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        Long count = stringRedisTemplate.opsForValue().increment(key);

        if (count == null) {
            return false;
        }
        if (count == 1) {
            stringRedisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }

        return count <= maxRequests;
    }
}
