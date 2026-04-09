package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;


import io.ketherlabs.postflow.identity.infrastructure.persistence.UserJpaEntity;
import io.ketherlabs.postflow.identity.infrastructure.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {


    private final UserJpaRepository jpa;

    @Override
    public void register(User user) {
        jpa.save(UserJpaEntity.from(user));
    }

    @Override
    public User findUserById(UUID userid) {

        Optional<UserJpaEntity> u = Optional.of(jpa.findById(userid).orElseThrow(
                () -> new IllegalArgumentException("cet utilisateur n'existe pas")
        ));
        return u.get().toDomain();
    }

    @Override
    public Optional<User> findByEmail(String email) {

        Optional<User> u = Optional.ofNullable(Optional.ofNullable(jpa.findByEmail(email)).orElseThrow(
                () -> new IllegalArgumentException("Aucun utiliateur avec cet email")
        ));
        return u;
    }

    @Override
    public boolean existsByEmail(String email) {

        User u = jpa.findByEmail(email);
        return u != null;
    }
}
