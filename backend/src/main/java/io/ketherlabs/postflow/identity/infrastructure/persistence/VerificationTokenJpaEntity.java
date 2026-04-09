package io.ketherlabs.postflow.identity.infrastructure.persistence;

import io.ketherlabs.postflow.core.EntityBase;
import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entité JPA représentant la table {@code verification_tokens}.
 *
 * <p>Sert uniquement de pont entre la base de données et l'entité domaine
 * {@link VerificationToken}. Ce n'est jamais exposée hors de la couche
 * infrastructure. Toute manipulation passe par
 * {@code VerificationTokenRepositoryAdapter}.
 *
 * <p>Les champs ne sont pas {@code final} — Hibernate en a besoin
 * pour les remplir via les setters après le constructeur sans arguments.
 */
@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationTokenJpaEntity extends EntityBase {


    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Convertit cette entité JPA en entité domaine {@link VerificationToken}.
     *
     * <p>Appelée uniquement par {@code VerificationTokenRepositoryAdapter}
     * lors d'une lecture depuis la base de données.
     *
     * @return une instance domaine reconstituée
     */
    public VerificationToken toDomain() {
        return VerificationToken.reconstruct(
                this.id,
                this.token,
                this.userId,
                this.expiresAt,
                this.used,
                this.createdAt
        );
    }

    /**
     * Construit une entité JPA depuis une entité domaine {@link VerificationToken}.
     *
     * <p>Appelée uniquement par {@code VerificationTokenRepositoryAdapter}
     * lors d'une écriture vers la base de données.
     *
     * @param token l'entité domaine source
     * @return une entité JPA prête à être persistée
     */
    public static VerificationTokenJpaEntity from(VerificationToken token) {
        VerificationTokenJpaEntity entity = new VerificationTokenJpaEntity();
        entity.id        = token.getId();
        entity.token     = token.getToken();
        entity.userId    = token.getUserid();
        entity.expiresAt = token.getExpriredAt();
        entity.used      = token.isUsed();
        entity.createdAt = token.getCreatedAt();
        return entity;
    }
}