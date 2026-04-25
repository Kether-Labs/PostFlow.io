package io.ketherlabs.postflow.core.security;

import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;

import java.util.HashSet;
import java.util.Set;

public class FakeRedisBlacklistAdapter implements RedisBlacklistPort {

    private final Set<String> blacklisted = new HashSet<>();

    void blacklistJti(String jti) { blacklisted.add(jti); }

    @Override public void    blacklist(String jti, long ttl) { blacklisted.add(jti); }
    @Override public boolean isBlacklisted(String jti)       { return blacklisted.contains(jti); }
}
