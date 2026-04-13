package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenRepositoryPort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake repository pour les tests unitaires
 * de {@code ResetPasswordUseCase} et {@code LogoutUseCase}.
 */
public class FakeRefreshTokenRepository
        implements RefreshTokenRepositoryPort {

    private final Map<String, RefreshToken> store = new HashMap<>();

    @Override
    public void save(RefreshToken token) {
        store.put(token.getTokenHash(), token);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return Optional.ofNullable(store.get(tokenHash));
    }

    @Override
    public List<RefreshToken> findByUserId(UUID userId) {
        return store.values().stream()
                .filter(t -> t.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void revoke(RefreshToken token) {
        token.revoke();
        store.put(token.getTokenHash(), token);
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        store.values().stream()
                .filter(t -> t.getUserId().equals(userId) && !t.isRevoked())
                .forEach(RefreshToken::markAsRevoke);
    }

    @Override
    public void deleteExpiredOrRevoked() {
        store.entrySet().removeIf(e ->
                e.getValue().isRevoked() || !e.getValue().isValid()
        );
    }

    public long countActiveByUserId(UUID userId) {
        return store.values().stream()
                .filter(t -> t.getUserId().equals(userId) && !t.isRevoked())
                .count();
    }
}