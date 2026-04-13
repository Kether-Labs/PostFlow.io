package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake repository pour les tests unitaires.
 * Stocke les utilisateurs en mémoire via une Map.
 * Aucune dépendance Spring ou base de données.
 */
public class FakeUserRepository implements UserRepositoryPort {

    private final Map<UUID, User> store = new HashMap<>();

    @Override
    public void register(User user) {
        store.put(user.getId(), user);
    }

    @Override
    public void save(User user) {
        // Même comportement que register — met à jour l'entrée existante
        store.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(u -> u.getEmail().getValue().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(u -> u.getEmail().getValue().equals(email));
    }

    public User findUserById(UUID userId) {
        return store.get(userId);
    }

    public long count() {
        return store.size();
    }
}