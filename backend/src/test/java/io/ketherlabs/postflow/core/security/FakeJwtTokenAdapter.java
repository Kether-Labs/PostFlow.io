package io.ketherlabs.postflow.core.security;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;

import java.util.UUID;

public class FakeJwtTokenAdapter implements JwtTokenPort {

    private static final String FIXED_JTI       = "jti-test-abc-123";
    private static final UUID FIXED_USER_ID   = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private boolean valid = true;

    void makeInvalid() { this.valid = false; }

    @Override public boolean isValid(String token)            { return valid; }
    @Override public String  extractJti(String token)         { return FIXED_JTI; }
    @Override public UUID extractUserId(String token)      { return FIXED_USER_ID; }
    @Override public String  generateAccessToken(User user)   { return "fake-token"; }
    @Override public long    getRemainingTtlSeconds(String t) { return 900L; }
}
