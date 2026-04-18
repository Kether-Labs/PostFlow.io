package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;

import java.util.*;

public class FakeJwtTokenAdapter implements JwtTokenPort {

    private final Map<String, TokenPayload> tokens = new HashMap<>();
    private final Set<String> invalidatedTokens = new HashSet<>();

    @Override
    public String generateAccessToken(User user) {
        String jti = UUID.randomUUID().toString();
        String token = "fake-access-token-" + jti;
        tokens.put(token, new TokenPayload(user.getId(), user.getEmail().getValue(), jti));
        return token;
    }

    @Override
    public String extractJti(String accessToken) {
        TokenPayload payload = tokens.get(accessToken);
        if (payload == null) {
            throw new IllegalArgumentException("Token inconnu : " + accessToken);
        }
        return payload.jti();
    }

    @Override
    public UUID extractUserId(String accessToken) {
        TokenPayload payload = tokens.get(accessToken);
        if (payload == null) {
            throw new IllegalArgumentException("Token inconnu : " + accessToken);
        }
        return payload.userId();
    }

    @Override
    public boolean isValid(String accessToken) {
        return tokens.containsKey(accessToken) && !invalidatedTokens.contains(accessToken);
    }

    public void invalidate(String accessToken) {
        invalidatedTokens.add(accessToken);
    }

    private record TokenPayload(UUID userId, String email, String jti) {}
}
