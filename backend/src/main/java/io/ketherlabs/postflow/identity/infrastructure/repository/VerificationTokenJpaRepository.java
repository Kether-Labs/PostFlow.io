package io.ketherlabs.postflow.identity.infrastructure.repository;

import io.ketherlabs.postflow.identity.infrastructure.persistence.VerificationTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour {@link VerificationTokenJpaEntity}.
 *
 * <p>Utilisée UNIQUEMENT par {@code VerificationTokenRepositoryAdapter}.
 * Aucun UseCase, aucun Controller ne l'injecte jamais directement.
 */
public interface VerificationTokenJpaRepository
        extends JpaRepository<VerificationTokenJpaEntity, UUID> {

    // UC-AUTH-002 VerifyEmailUseCase : charger le token par sa valeur
    Optional<VerificationTokenJpaEntity> findByToken(String token);

    // UC-AUTH-001 : invalider l'ancien token avant d'en générer un nouveau
    Optional<VerificationTokenJpaEntity> findByUserId(UUID userId);


    @Modifying
    @Query("DELETE FROM VerificationTokenJpaEntity t " +
            "WHERE t.expiresAt < :now OR t.used = true")
    void deleteExpiredOrUsed(@Param("now") Instant now);
}