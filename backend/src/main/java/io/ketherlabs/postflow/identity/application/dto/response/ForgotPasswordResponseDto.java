package io.ketherlabs.postflow.identity.application.dto.response;

/**
 * DTO de sortie pour POST /api/auth/forgot-password — HTTP 200.
 */
public record ForgotPasswordResponseDto(String message) {}