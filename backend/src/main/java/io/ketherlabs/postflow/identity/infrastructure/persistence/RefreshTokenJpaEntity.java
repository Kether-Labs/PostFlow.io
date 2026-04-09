package io.ketherlabs.postflow.identity.infrastructure.persistence;


import io.ketherlabs.postflow.core.EntityBase;
import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenJpaEntity extends EntityBase {

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    /**
     * Pas de @ManyToOne vers UserJpaEntity — on stocke juste l'UUID
     * Le domaine ne traverse pas les agrégats via des relations JPA.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    public RefreshToken toDomain() {
        return RefreshToken.reconstruct(
                this.id,
                this.tokenHash,
                this.expiresAt,
                this.userId,
                this.revoked,
                this.createdAt
        );
    }

    // Mapping Domain → Infrastructure
    public static RefreshTokenJpaEntity from(RefreshToken token) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.id        = token.getId();
        entity.tokenHash = token.getTokenHash();
        entity.userId    = token.getUserId();
        entity.expiresAt = token.getExpriresAt();
        entity.revoked   = token.isRevoked();
        entity.createdAt = Instant.from(token.getCreatedAt());
        return entity;
    }


}
