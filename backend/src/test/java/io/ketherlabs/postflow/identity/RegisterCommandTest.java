package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.usecase.input.RegisterCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Tests unitaires du DTO d'entrée {@link RegisterCommand}
 * du UseCase RegisterUseCase.
 * Valide toutes les contraintes d'entrée : présence,
 * format et longueur minimale des données.
 */

class RegisterCommandTest {

    @Test
    void should_throw_exception_when_firstname_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand(null, "Doe", "john@example.com", "password123"));
    }

    @Test
    void should_throw_exception_when_firstname_is_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("   ", "Doe", "john@example.com", "password123"));
    }

    @Test
    void should_throw_exception_when_firstname_too_short() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("Jo", "Doe", "john@example.com", "short"));
    }

    @Test
    void should_throw_exception_when_lastname_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", null, "john@example.com", "password123"));
    }

    @Test
    void should_throw_exception_when_lastname_is_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "   ", "john@example.com", "password123"));
    }

    @Test
    void should_throw_exception_when_lastname_too_short() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Do", "john@example.com", "short"));
    }

    @Test
    void should_throw_exception_when_email_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Doe", null, "password123"));
    }

    @Test
    void should_throw_exception_when_email_is_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Doe", "   ", "password123"));
    }

    @Test
    void should_throw_exception_when_email_is_invalid() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Doe", "john@example", "password123"));
    }

    @Test
    void should_throw_exception_when_password_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Doe", "john@example.com", null));
    }

    @Test
    void should_throw_exception_when_password_is_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Doe", "john@example.com", "   "));
    }

    @Test
    void should_throw_exception_when_password_too_short() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegisterCommand("John", "Doe", "john@example.com", "short"));
    }

    @Test
    void should_create_valid_command() {
        RegisterCommand cmd = new RegisterCommand("John", "Doe", "john@example.com", "securePass123");
        assertNotNull(cmd);
    }
}