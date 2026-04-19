package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;

import java.util.HashMap;
import java.util.Map;

/**
 * Fake adapter de blacklist Redis pour les tests unitaires.
 * Stocke les jti en memoire via une Map (jti -> ttlSeconds).
 * Aucune dependance Redis.
 */
public class FakeRedisBlackListAdapter implements RedisBlacklistPort {

    private final Map<String, Long> blacklistedTokens = new HashMap<>();

    @Override
    public void blacklist(String jti, long ttlSeconds) {
        blacklistedTokens.put(jti, ttlSeconds);
    }

    @Override
    public boolean isBlacklisted(String jti) {
        return blacklistedTokens.containsKey(jti);
    }

    public long getTtlSeconds(String jti) {
        return blacklistedTokens.getOrDefault(jti, 0L);
    }

    public int size() {
        return blacklistedTokens.size();
    }
}
