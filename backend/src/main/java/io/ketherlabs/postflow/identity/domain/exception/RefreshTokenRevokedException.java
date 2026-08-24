package io.ketherlabs.postflow.identity.domain.exception;

public class RefreshTokenRevokedException extends RuntimeException {
    public RefreshTokenRevokedException() {
        super("Refresh token revoked");
    }
}
