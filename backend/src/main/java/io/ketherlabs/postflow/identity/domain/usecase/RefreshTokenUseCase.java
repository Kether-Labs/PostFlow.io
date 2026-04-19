package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.exception.InvalidRefreshTokenException;
import io.ketherlabs.postflow.identity.domain.exception.RefreshTokenExpiredException;
import io.ketherlabs.postflow.identity.domain.exception.RefreshTokenRevokedException;
import io.ketherlabs.postflow.identity.domain.port.*;
import io.ketherlabs.postflow.identity.domain.usecase.output.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Use case UC-AUTH-004 : Rotation d'un Refresh Token.
 *
 * <p>Flux principal :
 * <ol>
 *   <li>Valider que le Refresh Token fourni n'est pas null ou vide</li>
 *   <li>Hasher le token reçu et le rechercher en base — sinon {@link InvalidRefreshTokenException}</li>
 *   <li>Vérifier TTL 7j — sinon {@link RefreshTokenExpiredException}</li>
 *   <li>Vérifier qu'il n'est pas révoqué — sinon {@link RefreshTokenRevokedException}</li>
 *   <li>Révoquer l'ancien token (rotation : usage unique)</li>
 *   <li>Charger l'utilisateur associé — sinon {@link InvalidRefreshTokenException}</li>
 *   <li>Générer un nouvel Access Token JWT via {@code JwtTokenPort}</li>
 *   <li>Générer un nouveau Refresh Token opaque via {@code RefreshTokenPort}</li>
 *   <li>Hasher et persister le nouveau Refresh Token (TTL 7j)</li>
 * </ol>
 */
public class RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtTokenPort               jwtTokenPort;
    private final UserRepositoryPort         userRepository;
    private final RefreshTokenPort           refreshTokenPort;
    private final PasswordEncoderPort        passwordEncoder;

    public RefreshTokenUseCase(RefreshTokenRepositoryPort refreshTokenRepository,
                               JwtTokenPort jwtTokenPort,
                               UserRepositoryPort userRepository,
                               RefreshTokenPort refreshTokenPort,
                               PasswordEncoderPort passwordEncoder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenPort           = jwtTokenPort;
        this.userRepository         = userRepository;
        this.refreshTokenPort       = refreshTokenPort;
        this.passwordEncoder        = passwordEncoder;
    }

    /**
     * Exécute la rotation d'un Refresh Token.
     *
     * @param refreshToken le Refresh Token opaque transmis par le client
     * @return une réponse contenant le nouvel Access Token JWT et le nouveau Refresh Token
     * @throws IllegalArgumentException        si le token est null ou vide
     * @throws InvalidRefreshTokenException    si le token est introuvable ou l'utilisateur associé inexistant
     * @throws RefreshTokenExpiredException    si le token a expiré (TTL 7j)
     * @throws RefreshTokenRevokedException    si le token a déjà été révoqué
     */
    @Transactional
    public LoginResponse execute(String refreshToken) {

        // 1. Valider l'entrée
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token must not be null or blank");
        }

        // 2. Hasher et rechercher le token en base
        String tokenHash = passwordEncoder.encode(refreshToken);
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(InvalidRefreshTokenException::new);

        // 3. Vérifier TTL 7j
        if (refreshTokenEntity.isExpired()) {
            throw new RefreshTokenExpiredException();
        }

        // 4. Vérifier que le token n'est pas révoqué
        if (refreshTokenEntity.isRevoked()) {
            throw new RefreshTokenRevokedException();
        }

        // 5. Révoquer l'ancien token (rotation : usage unique)
        refreshTokenEntity.markAsRevoke();

        // 6. Charger l'utilisateur associé
        User user = userRepository.findById(refreshTokenEntity.getUserId())
                .orElseThrow(InvalidRefreshTokenException::new);

        // 7. Générer un nouvel Access Token JWT
        String newAccessToken = jwtTokenPort.generateAccessToken(user);

        // 8. Générer un nouveau Refresh Token opaque
        String newRefreshToken = refreshTokenPort.generateRefreshToken(user);

        // 9. Hasher et persister le nouveau Refresh Token (TTL 7j)
        String newTokenHash = passwordEncoder.encode(newRefreshToken);
        RefreshToken newRefreshTokenEntity = RefreshToken.create(
                newTokenHash,
                Instant.now().plusSeconds(7 * 24 * 3600),
                user.getId()
        );
        refreshTokenRepository.save(newRefreshTokenEntity);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

}
