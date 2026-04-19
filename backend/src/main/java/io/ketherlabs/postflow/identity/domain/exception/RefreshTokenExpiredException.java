package io.ketherlabs.postflow.identity.domain.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException() {
        super("Refresh token expired");
    }
}
