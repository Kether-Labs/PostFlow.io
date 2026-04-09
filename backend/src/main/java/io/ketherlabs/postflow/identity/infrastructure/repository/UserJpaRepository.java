package io.ketherlabs.postflow.identity.infrastructure.repository;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.infrastructure.persistence.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    User findByEmail(String email);

}
