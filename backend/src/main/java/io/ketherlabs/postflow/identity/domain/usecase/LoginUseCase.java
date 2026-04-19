package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.exception.InvalidCredentialsException;
import io.ketherlabs.postflow.identity.domain.port.*;
import io.ketherlabs.postflow.identity.domain.usecase.input.LoginCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Use case UC-AUTH-003 : Authentification d'un utilisateur.
 *
 * <p>Flux principal :
 * <ol>
 *   <li>Charger l'utilisateur par email — sinon {@link InvalidCredentialsException}</li>
 *   <li>Vérifier que le compte est autorisé à se connecter (statut ACTIVE)</li>
 *   <li>Comparer le mot de passe saisi au hash stocké — sinon {@link InvalidCredentialsException}</li>
 *   <li>Générer un Access Token JWT via {@code JwtTokenPort}</li>
 *   <li>Générer un Refresh Token opaque via {@code RefreshTokenPort}</li>
 *   <li>Hasher le Refresh Token via {@code PasswordEncoderPort} et le persister (TTL 7j)</li>
 *   <li>Mettre à jour la date de dernière connexion sur l'utilisateur</li>
 * </ol>
 */
public class LoginUseCase {

    private final UserRepositoryPort           userRepositoryPort;
    private final JwtTokenPort                 jwtTokenPort;
    private final RefreshTokenRepositoryPort   refreshTokenRepositoryPort;
    private final RefreshTokenPort             refreshTokenPort;
    private final PasswordEncoderPort          passwordEncoder;

    public LoginUseCase(UserRepositoryPort userRepositoryPort,
                        JwtTokenPort jwtTokenPort,
                        RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                        RefreshTokenPort refreshTokenPort,
                        PasswordEncoderPort passwordEncoder) {
        this.userRepositoryPort         = userRepositoryPort;
        this.jwtTokenPort               = jwtTokenPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.refreshTokenPort           = refreshTokenPort;
        this.passwordEncoder            = passwordEncoder;
    }

    /**
     * Exécute l'authentification d'un utilisateur.
     *
     * @param command la commande contenant l'email et le mot de passe
     * @return une réponse contenant l'Access Token JWT et le Refresh Token opaque
     * @throws InvalidCredentialsException si l'email est inconnu ou le mot de passe invalide
     */
    @Transactional
    public LoginResponse execute(LoginCommand command) {

        // 1. Charger l'utilisateur par email
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. Vérifier que le compte est autorisé à se connecter (statut ACTIVE)
        user.assertCanLogin();

        // 3. Comparer le mot de passe saisi au hash stocké
        if (!passwordEncoder.matches(command.password(), user.getPassword().getHashedValue())) {
            throw new InvalidCredentialsException();
        }

        // 4. Générer un Access Token JWT
        String accessToken = jwtTokenPort.generateAccessToken(user);

        // 5. Générer un Refresh Token opaque
        String rawRefreshToken = refreshTokenPort.generateRefreshToken(user);

        // 6. Hasher et persister le Refresh Token (TTL 7j)
        String tokenHash = passwordEncoder.encode(rawRefreshToken);
        RefreshToken refreshToken = RefreshToken.create(
                tokenHash,
                Instant.now().plusSeconds(7 * 24 * 3600),
                user.getId()
        );
        refreshTokenRepositoryPort.save(refreshToken);

        // 7. Mettre à jour la date de dernière connexion
        user.updateLastLogin();

        return new LoginResponse(accessToken, rawRefreshToken);
    }

}
