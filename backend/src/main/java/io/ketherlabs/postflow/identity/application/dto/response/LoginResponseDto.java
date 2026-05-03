package io.ketherlabs.postflow.identity.application.dto.response;

/**
 * DTO de sortie pour POST /api/auth/login — HTTP 200.
 * Le refreshToken est aussi posé en cookie httpOnly par le controller.
 */
public record LoginResponseDto(
        String  accessToken,
        String  refreshToken,
        String  tokenType,
        long    expiresIn
) {
    public static LoginResponseDto from(String accessToken, String refreshToken) {
        return new LoginResponseDto(accessToken, refreshToken, "Bearer", 900L);
    }
}