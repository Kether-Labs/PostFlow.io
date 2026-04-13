package io.ketherlabs.postflow.identity.domain.exception;


/**
 * Lancée quand un token à usage unique a déjà été consommé.
 * Correspond à HTTP 400 TOKEN_ALREADY_USED.
 */
public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException() {
        super("token has already been uses");
    }
}
