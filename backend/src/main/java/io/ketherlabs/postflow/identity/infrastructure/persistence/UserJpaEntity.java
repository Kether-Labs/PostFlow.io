package io.ketherlabs.postflow.identity.infrastructure.persistence;


import io.ketherlabs.postflow.core.EntityBase;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;



@Entity
@Table(name = "_users")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserJpaEntity extends EntityBase {

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private Boolean emailVerified;

    @CreationTimestamp
    private Instant createdAt;
    private Instant lastLoginAt;

    /**
     * Mapping de l'infrastructure vers le domain
     * @return User from domain
     */
    public User toDomain() {
        return User.reconstruct(id, firstname, lastname, Email.of(this.email), Password.fromHash(this.password), status, emailVerified, createdAt, lastLoginAt);
    }

    /**
     * Mapping du domain vers l'infrastructure
     * @param user from domain
     * @return user Entity
     */
    public static UserJpaEntity from (User user) {
        UserJpaEntity u = new UserJpaEntity();
        u.id = user.getId();
        u.firstname = user.getFirstname();
        u.lastname = user.getLastname();
        u.email = user.getEmail().getValue();
        u.password = user.getPassword().getHashedValue();
        u.status        = user.getStatus();
        u.emailVerified = user.getEmailVerified();
        u.createdAt     = user.getCreatedAt();
        u.lastLoginAt   = user.getLastLoginAt();

        return u;
    }

}
