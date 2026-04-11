package io.ketherlabs.postflow.identity.domain.exception;

/**
 * Lancée quand un token a dépassé sa date d'expiration.
 * Correspond à HTTP 400 TOKEN_EXPIRED.
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("token has exprired");
    }
}
