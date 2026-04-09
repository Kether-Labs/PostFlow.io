package io.ketherlabs.postflow.identity.domain.usecase.output;

import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;

import java.time.Instant;

public record UserReponseResult(
        String firstname,
        String lastname,
        String email,
        String password,
        UserStatus status,
        Boolean emailVerified,
        Instant createdAt
) {
}
