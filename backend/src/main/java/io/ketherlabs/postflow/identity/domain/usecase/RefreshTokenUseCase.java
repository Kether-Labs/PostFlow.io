package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.RefreshTokenCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.RefreshTokenResponse;
import org.springframework.transaction.annotation.Transactional;

public class RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtTokenPort jwtTokenPort;

    public RefreshTokenUseCase(
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            JwtTokenPort jwtTokenPort
    ) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Transactional
    public RefreshTokenResponse execute(RefreshTokenCommand command) {

        RefreshToken token = refreshTokenRepositoryPort.findByTokenHash(command.token())
                .orElseThrow(InvalidTokenException::new);

        if (token.isExpired() || token.isRevoked()) {
            throw new InvalidTokenException();
        }

        User user = userRepositoryPort.findById(token.getUserId())
                .orElseThrow(InvalidTokenException::new);

        String newAccessToken = jwtTokenPort.generateAccessToken(user);

        return new RefreshTokenResponse(newAccessToken);
    }
}
