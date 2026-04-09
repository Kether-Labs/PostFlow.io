package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;
import io.ketherlabs.postflow.identity.domain.port.VerificationTokenRepositoryPort;
import io.ketherlabs.postflow.identity.infrastructure.persistence.VerificationTokenJpaEntity;
import io.ketherlabs.postflow.identity.infrastructure.repository.VerificationTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptateur sortant du port {@link VerificationTokenRepositoryPort}.
 *
 * <p>Seul point du système où {@link VerificationTokenJpaEntity} est manipulée.
 * Toutes les conversions Domain ↔ Infrastructure sont encapsulées ici.
 * Aucun UseCase, aucun Controller n'accède jamais directement
 * à {@link VerificationTokenJpaRepository}.
 */
@Component
@RequiredArgsConstructor
public class VerificationTokenRepositoryAdapter
        implements VerificationTokenRepositoryPort {

    private final VerificationTokenJpaRepository jpa;

    /**
     * Persiste un token de vérification email.
     *
     * <p>Appelée par {@code RegisterUseCase} (UC-AUTH-001) après
     * génération du token, et par {@code ResendVerificationUseCase}
     * lors du renvoi d'un nouveau token.
     *
     * @param token le token domaine à persister
     */
    @Override
    public void save(VerificationToken token) {
        jpa.save(VerificationTokenJpaEntity.from(token));
    }

    /**
     * Recherche un token par sa valeur.
     *
     * <p>Appelée par {@code VerifyEmailUseCase} (UC-AUTH-002)
     * pour valider le token reçu depuis le lien email.
     *
     * @param token la valeur du token reçu dans le lien
     * @return un {@code Optional} contenant le token domaine si trouvé
     */
    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return jpa.findByToken(token)
                .map(VerificationTokenJpaEntity::toDomain);
    }

    /**
     * Recherche le token actif d'un utilisateur.
     *
     * <p>Appelée par {@code ResendVerificationUseCase} pour invalider
     * l'ancien token avant d'en générer un nouveau.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return un {@code Optional} contenant le token domaine si trouvé
     */
    @Override
    public Optional<VerificationToken> findByUserId(UUID userId) {
        return jpa.findByUserId(userId)
                .map(VerificationTokenJpaEntity::toDomain);
    }

    /**
     * Marque un token comme utilisé après vérification email réussie.
     *
     * <p>Appelée par {@code VerifyEmailUseCase} (UC-AUTH-002) — étape 6.
     * On passe par la méthode métier {@link VerificationToken#markAsUsed()}
     * avant de persister — la règle métier reste dans le domaine.
     *
     * @param token le token domaine à marquer comme utilisé
     */
    @Override
    public void markAsUsed(VerificationToken token) {
        token.markAsUsed();
        jpa.save(VerificationTokenJpaEntity.from(token));
    }

    /**
     * Supprime les tokens expirés ou déjà utilisés.
     *
     * <p>Destinée à être appelée par un job de nettoyage {@code @Scheduled}.
     */
    @Override
    public void deleteExpiredOrUsed() {
        jpa.deleteExpiredOrUsed(Instant.now());
    }
}