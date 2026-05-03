package io.ketherlabs.postflow.identity.application.dto.request;

/**
 * DTO d'entrée pour POST /api/auth/logout.
 * refreshToken optionnel — peut venir du cookie httpOnly.
 */
public record LogoutRequest(String refreshToken) {}