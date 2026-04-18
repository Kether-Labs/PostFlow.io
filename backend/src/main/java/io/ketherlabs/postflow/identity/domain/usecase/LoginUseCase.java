package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.exception.InvalidCredentialsException;
import io.ketherlabs.postflow.identity.domain.port.*;
import io.ketherlabs.postflow.identity.domain.usecase.input.LoginCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public class LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtTokenPort jwtTokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final RefreshTokenPort refreshTokenPort;
    private final PasswordEncoderPort passwordEncoder;

    public LoginUseCase(UserRepositoryPort userRepositoryPort,
                        JwtTokenPort jwtTokenPort,
                        RefreshTokenRepositoryPort refreshTokenRepositoryPort,
                        RefreshTokenPort refreshTokenPort,
                        PasswordEncoderPort passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.jwtTokenPort = jwtTokenPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.refreshTokenPort = refreshTokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponse execute(LoginCommand command) {

        // Charger l'utilisateur de la base de donnees
        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        // Verification du status du compte de l'utilisateur (doit être ACTIVE pour se connecter)
        user.assertCanLogin();

        // Comparaison du mot de passe avec celui enregistré dans la base de donnees
        if (!passwordEncoder.matches(command.password(), user.getPassword().getHashedValue())) {
            throw new InvalidCredentialsException();
        }

        // Génère un JWT access token
        String accessToken = jwtTokenPort.generateAccessToken(user);

        // Génère un Refresh Token opaque (UUID hashé et persisté)
        String rawRefreshToken = refreshTokenPort.generateRefreshToken(user);
        String tokenHash = passwordEncoder.encode(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.create(
                tokenHash,
                Instant.now().plusSeconds(7 * 24 * 3600),   // TTL 7j
                user.getId()
        );
        refreshTokenRepositoryPort.save(refreshToken);

        // Enregistre la date de dernière connexion
        user.updateLastLogin();

        // Retourne la réponse avec les tokens
        return new LoginResponse(accessToken, rawRefreshToken);
    }

}
