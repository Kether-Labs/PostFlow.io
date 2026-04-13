package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.port.HmacTokenGeneratorPort;

/**
 * Fake générateur de token pour les tests unitaires.
 * Retourne toujours le même token prévisible
 * pour faciliter les assertions.
 */
public class FakeHmacTokenGenerator implements HmacTokenGeneratorPort {

    public static final String FAKE_TOKEN = "fake-reset-token-abc123";

    @Override
    public String generate() {
        return FAKE_TOKEN;
    }
}