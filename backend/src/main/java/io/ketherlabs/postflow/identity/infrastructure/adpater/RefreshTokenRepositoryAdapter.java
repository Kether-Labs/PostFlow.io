package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenRepositoryPort;
import io.ketherlabs.postflow.identity.infrastructure.persistence.RefreshTokenJpaEntity;
import io.ketherlabs.postflow.identity.infrastructure.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {


    private final RefreshTokenJpaRepository jpa;

    @Override
    public void save(RefreshToken token) {
        jpa.save(RefreshTokenJpaEntity.from(token));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String token) {
        return jpa.findByTokenHash(token);
    }

    @Override
    public void revoke(RefreshToken token) {
        token.markAsRevoke();
        jpa.save(RefreshTokenJpaEntity.from(token));
    }

    @Override
    public void deleteExpiredOrRevoked() {
       // jpa.deleteExpiredOrRevoked(Instant.now());
    }
}
