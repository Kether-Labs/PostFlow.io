package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant pour la persistance des tokens de vérification email.
 *
 * <p>Interface pure — aucune annotation, aucun import Spring ou JPA.
 * Implémentée par {@code VerificationTokenRepositoryAdapter}
 * dans la couche infrastructure.
 */
public interface VerificationTokenRepositoryPort {

    void save(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserId(UUID userId);

    void markAsUsed(VerificationToken token);

    void deleteExpiredOrUsed();
}