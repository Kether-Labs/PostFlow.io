package io.ketherlabs.postflow.identity.domain.entity;

import java.time.Instant;
import java.util.UUID;

import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.*;

public class User {

    public final UUID id;
    private final String firstname;
    private final String lastname;
    private final Email email;
    private final Password password;

    private final UserStatus status;
    private final Boolean emailVerified;
    private final Instant createdAt;
    private final Instant lastLoginAt;


    private User(UUID id, String firstname, String lastname, Email email, Password password, UserStatus status, Boolean emailVerified, Instant createdAt, Instant lastLoginAt) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.status = status;
        this.emailVerified = emailVerified;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    public static User create() {
        return null;
    }

    public static User reconstruct(UUID id,
                                   String firstname,
                                   String lastname,
                                   Email email,
                                   Password hashedPassword,
                                   UserStatus status,
                                   boolean emailVerified,
                                   Instant createdAt,
                                   Instant lastLoginAt) {
        return new User(id, firstname, lastname, email, hashedPassword, status,
                emailVerified, createdAt, lastLoginAt);
    }
}
