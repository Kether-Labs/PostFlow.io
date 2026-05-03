package io.ketherlabs.postflow.identity.application.dto.response;

/**
 * DTO de sortie pour POST /api/auth/verify-email — HTTP 200.
 */
public record VerifyEmailResponseDto(boolean success, String message) {}