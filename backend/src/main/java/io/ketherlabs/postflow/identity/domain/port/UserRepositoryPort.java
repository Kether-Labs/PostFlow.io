package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Port sortant définissant le contrat de persistance des utilisateurs.
 *
 * <p>Interface pure — aucune annotation, aucun import Spring ou JPA.
 * Implémentée par {@code UserRepositoryAdapter} dans la couche infrastructure.
 */
public interface UserRepositoryPort {

    /**
     * Persiste un nouvel utilisateur lors de l'inscription.
     * Utilisée par {@code RegisterUseCase} (UC-AUTH-001).
     */
    void register(User user);

    /**
     * Met à jour un utilisateur existant.
     * Utilisée par {@code VerifyEmailUseCase}, {@code ResetPasswordUseCase}.
     */
    void save(User user);

    /**
     * Recherche un utilisateur par son identifiant unique.
     *
     * @param userId l'identifiant de l'utilisateur
     * @return un {@code Optional} contenant l'utilisateur si trouvé
     */
    Optional<User> findById(UUID userId);

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email l'email normalisé
     * @return un {@code Optional} contenant l'utilisateur si trouvé
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email est déjà utilisé.
     *
     * @param email l'email à vérifier
     * @return {@code true} si l'email existe déjà en base
     */
    boolean existsByEmail(String email);

    User findUserById(UUID userid);
}