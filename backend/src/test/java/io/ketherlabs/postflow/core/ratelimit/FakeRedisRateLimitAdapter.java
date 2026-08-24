package io.ketherlabs.postflow.core.ratelimit;

import io.ketherlabs.postflow.core.domain.port.RateLimitPort;

import java.util.HashMap;
import java.util.Map;


/**
 * Fake qui reproduit le comportement réel du compteur par clé Redis.
 * Compte les appels pour chaque clé et retourne false dès que maxRequests est dépassé.
 */
public class FakeRedisRateLimitAdapter implements RateLimitPort {

    private final Map<String, Integer> counters = new HashMap<>();

    @Override
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        int count = counters.merge(key, 1, Integer::sum);
        return count <= maxRequests;
    }

    int countCallsFor(String keyPrefix) {
        return counters.entrySet().stream()
                .filter(e -> e.getKey().startsWith(keyPrefix))
                .mapToInt(Map.Entry::getValue)
                .sum();
    }
}
