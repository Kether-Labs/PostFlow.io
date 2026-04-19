package io.ketherlabs.postflow.identity.domain.exception;

public class RefreshTokenRevokedException extends RuntimeException {
    public RefreshTokenRevokedException(String refreshToken) {
        super("Refresh token revoked: " + refreshToken);
    }
}
