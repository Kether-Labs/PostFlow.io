package io.ketherlabs.postflow.identity.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest (
        @NotBlank(message = "Token is required")
        String token
){
}
