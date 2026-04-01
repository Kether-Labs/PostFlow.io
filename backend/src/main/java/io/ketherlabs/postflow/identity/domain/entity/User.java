package io.ketherlabs.postflow.identity.domain.entity;

import java.time.Instant;
import java.util.UUID;

import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.*;
import io.ketherlabs.postflow.identity.exception.AccountNotVerifiedException;
import io.ketherlabs.postflow.identity.exception.AccountSuspendedException;

public class User {

    private final UUID id;
    private final String firstname;
    private final String lastname;
    private final Email email;
    private Password password;

    private UserStatus status;
    private final Boolean emailVerified;
    private final Instant createdAt;
    private Instant lastLoginAt;


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

    /**
     * Methode Factory permettant de cree un nouvelutiliateur
     * @param firstname le username
     * @param lastname son prenom
     * @param email son email (verifie dans le value object)
     * @param password son mot de passe
     * @return un nouvel utilisateur
     */
    public static User create(String firstname, String lastname, Email email, Password password) {

        if (firstname.isBlank() || lastname.isBlank()) {
            throw new IllegalArgumentException("le nom et le prenom doivent être renseigné");
        }
        return new User(
                UUID.randomUUID(),
                firstname,
                lastname,
                email,
                password,
                UserStatus.PENDING,
                false,
                Instant.now(),
                Instant.now()
        );

    }

    /**
     * nous permet d'activé le compte si
     * l'email est vérifier
     */
    public void activate() {
        if (status == UserStatus.SUSPENDED) {
            throw new IllegalStateException("On ne peut pas activé un compte suspendu");
        }
        this.status = UserStatus.ACTIVE;
    }

    /**
     * Methode Factory qui vas nous permettre de reconstruire la classe
     */
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

    /**
     * Vérifie que l'utilisateur peut se connecter.
     * Utilisé par LoginUseCase avant d'émettre les tokens.
     */
    public void assertCanLogin() {
        if (this.status == UserStatus.PENDING) {
            throw new AccountNotVerifiedException(this.email.getValue());
        }
        if (this.status == UserStatus.SUSPENDED) {
            throw new AccountSuspendedException(this.email.getValue());
        }
    }

    /**
     * Met à jour le mot de passe (change-password / reset-password).
     * Le nouveau hash est fourni par le use case (le domaine ne hash pas lui-même).
     */
    public void changePassword(Password newHashedPassword) {
        this.password = newHashedPassword;
    }

    /**
     * Enregistre la date de dernière connexion.
     */
    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }


    public UUID getId() { return id; }

    public String getFirstname() { return firstname; }

    public String getLastname() { return lastname; }

    public Email getEmail() { return email; }

    public Password getPassword() { return password; }

    public UserStatus getStatus() { return status; }

    public Boolean getEmailVerified() { return emailVerified; }

    public Instant getCreatedAt() { return createdAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
}
