package io.ketherlabs.postflow.identity.domain.usecase.output;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
