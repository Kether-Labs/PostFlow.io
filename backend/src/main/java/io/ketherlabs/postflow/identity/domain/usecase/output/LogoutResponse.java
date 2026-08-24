package io.ketherlabs.postflow.identity.domain.usecase.output;

public record LogoutResponse(boolean success, String message) {
            public LogoutResponse {
                if (message == null || message.isBlank()) throw new IllegalArgumentException("Message is required");
            }
}
