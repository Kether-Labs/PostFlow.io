package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;

import java.time.Instant;
import java.util.*;

public class FakeJwtTokenAdapter implements JwtTokenPort {

    private final Map<String, TokenPayload> tokens = new HashMap<>();

    @Override
    public String generateAccessToken(User user) {
        String jti = UUID.randomUUID().toString();
        String token = "fake-access-token-" + jti;
        Instant expiresAt = Instant.now().plusSeconds(900L);
        tokens.put(token, new TokenPayload(user.getId(), user.getEmail().getValue(), jti, expiresAt));
        return token;
    }

    @Override
    public String extractJti(String accessToken) {
        TokenPayload payload = tokens.get(accessToken);
        if (payload == null) {
            throw new InvalidTokenException();
        }
        return payload.jti();
    }

    @Override
    public UUID extractUserId(String accessToken) {
        TokenPayload payload = tokens.get(accessToken);
        if (payload == null) {
            throw new InvalidTokenException();
        }
        return payload.userId();
    }

    @Override
    public boolean isValid(String accessToken) {
        return tokens.containsKey(accessToken) && tokens.get(accessToken).expiresAt().isAfter(Instant.now());
    }

    @Override
    public long getRemainingTtlSeconds(String accessToken) {
        if (!tokens.containsKey(accessToken)) {
            throw new InvalidTokenException();
        }
        return 900L;
    }

    private record TokenPayload(UUID userId, String email, String jti, Instant expiresAt) {}
}
