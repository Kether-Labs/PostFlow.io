package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.port.PasswordResetTokenRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Fake repository pour les tests unitaires de
 * {@code ForgotPasswordUseCase} et {@code ResetPasswordUseCase}.
 */
public class FakePasswordResetTokenRepository
        implements PasswordResetTokenRepositoryPort {

    private final Map<String, PasswordResetToken> store = new HashMap<>();

    @Override
    public void save(PasswordResetToken token) {
        store.put(token.getTokenHash(), token);
    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return Optional.ofNullable(store.get(tokenHash));
    }

    @Override
    public void markAsUsed(PasswordResetToken token) {
        token.markAsUsed();
        store.put(token.getTokenHash(), token);
    }

    @Override
    public void deleteExpiredOrUsed() {
        store.entrySet().removeIf(e ->
                e.getValue().isUsed() || e.getValue().isExpired()
        );
    }

    public long count() {
        return store.size();
    }
}