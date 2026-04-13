package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;
import io.ketherlabs.postflow.identity.domain.port.VerificationTokenRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake repository pour les tests unitaires de {@code VerifyEmailUseCase}.
 * Stocke les tokens en mémoire via une Map.
 * Aucune dépendance Spring ou base de données.
 */
public class FakeVerificationTokenRepository
        implements VerificationTokenRepositoryPort {

    private final Map<String, VerificationToken> storeByToken = new HashMap<>();
    private final Map<UUID, VerificationToken>   storeByUser  = new HashMap<>();

    @Override
    public void save(VerificationToken token) {
        storeByToken.put(token.getToken(), token);
        storeByUser.put(token.getUserid(), token);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return Optional.ofNullable(storeByToken.get(token));
    }

    @Override
    public Optional<VerificationToken> findByUserId(UUID userId) {
        return Optional.ofNullable(storeByUser.get(userId));
    }

    @Override
    public void markAsUsed(VerificationToken token) {
        token.markAsUsed();
        storeByToken.put(token.getToken(), token);
        storeByUser.put(token.getUserid(), token);
    }

    @Override
    public void deleteExpiredOrUsed() {
        storeByToken.entrySet().removeIf(e -> e.getValue().isUsed()
                || e.getValue().isExpired());
        storeByUser.entrySet().removeIf(e -> e.getValue().isUsed()
                || e.getValue().isExpired());
    }
}