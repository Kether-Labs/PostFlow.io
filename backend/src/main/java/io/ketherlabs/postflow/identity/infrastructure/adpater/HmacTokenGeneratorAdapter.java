package io.ketherlabs.postflow.identity.infrastructure.adpater;

import io.ketherlabs.postflow.identity.domain.port.HmacTokenGeneratorPort;

import java.security.SecureRandom;
import java.util.Base64;

public class HmacTokenGeneratorAdapter implements HmacTokenGeneratorPort {


    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;

    /**
     * {@inheritDoc}
     *
     * <p>Génère 32 bytes aléatoires via {@link SecureRandom}
     * et les encode en Base64 URL-safe sans padding.
     *
     * @return token aléatoire sécurisé de 43 caractères
     */
    @Override
    public String generate() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
