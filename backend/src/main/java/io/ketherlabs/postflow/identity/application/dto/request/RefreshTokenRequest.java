package io.ketherlabs.postflow.identity.application.dto.request;

/**
 * DTO d'entrée pour POST /api/auth/refresh.
 * Le token peut venir du body ou du cookie httpOnly
 * — les deux champs sont optionnels ici.
 */
public record RefreshTokenRequest(String refreshToken) {}