package io.ketherlabs.postflow.identity.infrastructure.adpater;


import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.port.PasswordResetTokenRepositoryPort;
import io.ketherlabs.postflow.identity.infrastructure.persistence.PasswordResetTokenJpaEntity;
import io.ketherlabs.postflow.identity.infrastructure.repository.PasswordResetTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;


/**
 * Adaptateur sortant du port {@link PasswordResetTokenRepositoryPort}.
 *
 * <p>Seul point du système où {@link PasswordResetTokenJpaEntity} est manipulée.
 * Toutes les conversions Domain ↔ Infrastructure sont encapsulées ici.
 * Aucun UseCase, aucun Controller n'accède jamais directement
 * à {@link PasswordResetTokenJpaRepository}.
 */
@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter
        implements PasswordResetTokenRepositoryPort {

    private final PasswordResetTokenJpaRepository jpa;

    /**
     * Persiste un token de réinitialisation de mot de passe.
     *
     * <p>Utilisée par {@code ForgotPasswordUseCase} (UC-AUTH-006)
     * après génération du token HMAC.
     *
     * @param token le token domaine à persister
     */
    @Override
    public void save(PasswordResetToken token) {
        jpa.save(PasswordResetTokenJpaEntity.from(token));
    }

    /**
     * Recherche un token par son hash.
     *
     * <p>Utilisée par {@code ResetPasswordUseCase} (UC-AUTH-007)
     * pour valider le token reçu depuis le lien email.
     *
     * @param tokenHash le hash HMAC-SHA256 du token
     * @return un {@code Optional} contenant le token domaine si trouvé
     */
    @Override
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return jpa.findByTokenHash(tokenHash)
                .map(PasswordResetTokenJpaEntity::toDomain);
    }

    /**
     * Marque un token comme utilisé après un reset de mot de passe réussi.
     *
     * <p>Utilisée par {@code ResetPasswordUseCase} (UC-AUTH-007) — étape 8 :
     * "UPDATE token SET used = true". On passe par la méthode métier
     * {@link PasswordResetToken#markAsUsed()} avant de persister.
     *
     * @param token le token domaine à marquer comme utilisé
     */
    @Override
    public void markAsUsed(PasswordResetToken token) {
        token.markAsUsed();
        jpa.save(PasswordResetTokenJpaEntity.from(token));
    }

    /**
     * Supprime les tokens expirés ou déjà utilisés.
     *
     * <p>Destinée à être appelée par un job de nettoyage {@code @Scheduled}.
     * Réduit la taille de la table et limite l'exposition de données sensibles.
     */
    @Override
    public void deleteExpiredOrUsed() {
        jpa.deleteExpiredOrUsed(Instant.now());
    }
}