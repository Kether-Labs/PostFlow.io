package io.ketherlabs.postflow.identity.application.dto.response;

/**
 * DTO de sortie pour POST /api/auth/reset-password — HTTP 200.
 */
public record ResetPasswordResponseDto(String message) {}