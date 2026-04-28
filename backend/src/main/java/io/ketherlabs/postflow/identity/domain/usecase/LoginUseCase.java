package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.exception.InvalidCredentialsException;
import io.ketherlabs.postflow.identity.domain.port.*;
import io.ketherlabs.postflow.identity.domain.usecase.input.LoginCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.LoginResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtTokenPort jwtTokenPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public LoginUseCase(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            JwtTokenPort jwtTokenPort,
            RefreshTokenRepositoryPort refreshTokenRepositoryPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtTokenPort = jwtTokenPort;
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
    }

    @Transactional
    public LoginResponse execute(LoginCommand command) {

        User user = userRepositoryPort.findByEmail(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoderPort.matches(
                command.password(),
                user.getPassword().getHashedValue()
        )) {
            throw new InvalidCredentialsException();
        }


        String accessToken = jwtTokenPort.generateAccessToken(user);


        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.create(
                refreshTokenValue,
                Instant.now().plus(Duration.ofDays(7)),
                user.getId()
        );

        refreshTokenRepositoryPort.save(refreshToken);

        return new LoginResponse(
                accessToken,
                refreshTokenValue
        );
    }
}