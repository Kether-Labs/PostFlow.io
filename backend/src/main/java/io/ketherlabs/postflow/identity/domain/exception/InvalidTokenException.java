package io.ketherlabs.postflow.identity.domain.exception;


/**
 * Lancée quand un token est introuvable en base
 * ou que sa signature est invalide.
 * Correspond à HTTP 400 INVALID_TOKEN.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super("token is invalid or not found");
    }
}
