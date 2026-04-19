package io.ketherlabs.postflow.identity.domain.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String refreshToken) {
        super("Refresh token expired: " + refreshToken);
    }
}
