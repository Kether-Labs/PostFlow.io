package io.ketherlabs.postflow.identity.infrastructure.repository;

import io.ketherlabs.postflow.identity.infrastructure.persistence.PasswordResetTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface PasswordResetTokenJpaRepository extends JpaRepository<PasswordResetTokenJpaEntity, UUID> {
}
