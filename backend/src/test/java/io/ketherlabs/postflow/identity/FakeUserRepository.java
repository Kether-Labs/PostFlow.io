package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Fake Repository pour les tests unitaires.
 * Implémente UserRepositoryPort en stockant les utilisateurs en mémoire
 * via une Map, sans dépendre d'une base de données réelle.
 * Permet de tester la logique métier de manière isolée et rapide.
 */
public class FakeUserRepository implements UserRepositoryPort {

    private final Map<UUID, User> store = new HashMap<>();

    @Override
    public void register(User user) {
        store.put(user.getId(), user);
    }

    @Override
    public User findUserById(UUID userid) {
        return store.get(userid);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.values().stream()
                .filter(user -> user.getEmail().getValue().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.values().stream()
                .anyMatch(user -> user.getEmail().getValue().equals(email));
    }

    public long count() {
        return store.size();
    }
}
