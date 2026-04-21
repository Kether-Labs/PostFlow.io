package io.ketherlabs.postflow.identity.domain.usecase.input;

public record LogoutCommand(
        String accessToken,
        String refreshToken
) {

    public LogoutCommand {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token must not be null or blank");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be null or blank");
        }
    }
}
