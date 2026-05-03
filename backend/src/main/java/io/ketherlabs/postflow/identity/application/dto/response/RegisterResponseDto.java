package io.ketherlabs.postflow.identity.application.dto.response;

import java.util.UUID;

/**
 * DTO de sortie pour POST /api/auth/register — HTTP 201.
 */
public record RegisterResponseDto(
        UUID userId,
        String email,
        String status,
        String message
) {}