package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenPort;

public class FakeRefreshTokenAdapter implements RefreshTokenPort {
    @Override
    public String generateRefreshToken(User user) {
        return "fake-refresh-token-for-user-" + user.getId();
    }

    @Override
    public boolean isValid(String tokenHash) {
        return tokenHash != null && !tokenHash.isBlank();
    }
}
