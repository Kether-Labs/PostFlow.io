package io.ketherlabs.postflow.identity.domain.usecase.output;

public record LoginResponse(String accessToken, String refreshToken) {
        public LoginResponse {
            if (accessToken == null || accessToken.isBlank()) throw new IllegalArgumentException("Access token is required");
        }
public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
