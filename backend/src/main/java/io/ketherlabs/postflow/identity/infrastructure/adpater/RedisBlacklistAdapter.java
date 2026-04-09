package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Adaptateur sortant implémentant {@link RedisBlacklistPort}.
 *
 * <p>Stocke les {@code jti} révoqués dans Redis sous la forme :
 * <pre>
 *   clé   : blacklist:{jti}
 *   valeur : "1"
 *   TTL   : temps résiduel du token en secondes
 * </pre>
 *
 * <p>Redis supprime automatiquement les entrées expirées — la blacklist
 * ne grossit pas indéfiniment. Un {@code jti} absent de Redis signifie
 * que le token est soit valide, soit naturellement expiré.
 *
 * <p>Utilise {@code StringRedisTemplate} (plus léger que {@code RedisTemplate})
 * car les clés et valeurs sont de simples chaînes de caractères.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisBlacklistAdapter implements RedisBlacklistPort {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private final StringRedisTemplate redisTemplate;

    /**
     * {@inheritDoc}
     *
     * <p>Opération Redis : {@code SET blacklist:{jti} "1" EX {ttlSeconds}}
     * Équivalent au SETNX de la spec — on utilise {@code set()} avec TTL
     * qui est atomique et suffisant ici (un même jti ne peut pas être
     * blacklisté deux fois, car il expire avec le token).
     *
     * @param jti        le JWT ID à blacklister
     * @param ttlSeconds TTL en secondes — doit être > 0
     */
    @Override
    public void blacklist(String jti, long ttlSeconds) {
        if (ttlSeconds <= 0) {
            // Token déjà expiré — inutile de le stocker
            log.debug("Token jti={} already expired, skipping blacklist", jti);
            return;
        }
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
        log.debug("Token blacklisted : jti={}, ttl={}s", jti, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Opération Redis : {@code EXISTS blacklist:{jti}}
     * Retourne {@code true} si la clé existe (token révoqué),
     * {@code false} si absente (token valide ou naturellement expiré).
     *
     * @param jti le JWT ID à vérifier
     * @return {@code true} si blacklisté
     */
    @Override
    public boolean isBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }
}