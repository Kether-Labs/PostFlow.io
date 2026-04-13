package io.ketherlabs.postflow.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Événement domaine publié après activation du compte utilisateur
 * suite à la vérification email (UC-AUTH-002).
 */
public record UserActivatedEvent(
        UUID userId,
        String email,
        Instant occurredAt
) {
    public UserActivatedEvent(UUID userId, String email) {
        this(userId, email, Instant.now());
    }
}