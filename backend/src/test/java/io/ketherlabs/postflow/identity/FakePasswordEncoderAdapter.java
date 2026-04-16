package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.port.PasswordEncoderPort;

/**
 * Fake encodeur de mot de passe pour les tests unitaires.
 * Simule le hashage sans BCrypt pour des tests rapides.
 */
public class FakePasswordEncoderAdapter implements PasswordEncoderPort {

    @Override
    public String encode(String rawPassword) {
        return "hashed_" + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return hashedPassword.equals("hashed_" + rawPassword);
    }
}