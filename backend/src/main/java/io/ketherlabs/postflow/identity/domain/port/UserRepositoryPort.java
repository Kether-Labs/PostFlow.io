package io.ketherlabs.postflow.identity.domain.port;

import io.ketherlabs.postflow.identity.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    User findUserById(UUID userid);

    Optional<User> findByEmail(String email);

   boolean existsByEmail(String email);
}
