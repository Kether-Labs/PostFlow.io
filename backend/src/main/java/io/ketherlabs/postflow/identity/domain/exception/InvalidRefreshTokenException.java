package io.ketherlabs.postflow.identity.domain.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String refreshToken) {
        super("Invalid refresh token: " + refreshToken);
    }
}
