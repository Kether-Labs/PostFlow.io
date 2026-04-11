package io.ketherlabs.postflow.identity.infrastructure.repository;

import io.ketherlabs.postflow.identity.infrastructure.persistence.PasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {

    // UC-AUTH-007 ResetPasswordUseCase : charger le token par son hash
    Optional<PasswordResetTokenJpaEntity> findByTokenHash(String tokenHash);

    // Nettoyage : supprime les tokens expirés ou déjà utilisés
    @Modifying
    @Query("DELETE FROM PasswordResetTokenJpaEntity t " +
            "WHERE t.expiresAt < :now OR t.used = true")
    void deleteExpiredOrUsed(@Param("now") Instant now);
}
