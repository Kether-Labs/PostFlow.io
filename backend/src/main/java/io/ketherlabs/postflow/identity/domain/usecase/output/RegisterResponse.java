package io.ketherlabs.postflow.identity.domain.usecase.output;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String email,
        String message
) {
}
