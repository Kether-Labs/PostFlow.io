package io.ketherlabs.postflow.identity.infrastructure.adpater;


import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.port.PasswordResetTokenRepositoryPort;
import io.ketherlabs.postflow.identity.infrastructure.repository.PasswordResetTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;



@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpa;

    @Override
    public void save(PasswordResetToken token) {

    }

    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return Optional.empty();
    }

    @Override
    public void markAsUsed(PasswordResetToken token) {

    }

    @Override
    public void deleteExpiredOrUsed() {

    }
}
