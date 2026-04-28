package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.LogoutCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.LogoutResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class LogoutUseCase {

    private final JwtTokenPort jwtTokenPort;
    private final RedisBlacklistPort redisBlacklistPort;

    public LogoutUseCase(
            JwtTokenPort jwtTokenPort,
            RedisBlacklistPort redisBlacklistPort
    ) {
        this.jwtTokenPort = jwtTokenPort;
        this.redisBlacklistPort = redisBlacklistPort;
    }

    @Transactional
    public LogoutResponse execute(LogoutCommand command) {

        if (command.accessToken() == null) {
            return new LogoutResponse(true, "Already logged out");
        }

        String jti = jwtTokenPort.extractJti(command.accessToken());

        // TTL estimé
        long ttl = Instant.now()
                .plus(15, ChronoUnit.MINUTES)
                .getEpochSecond() - Instant.now().getEpochSecond();

        redisBlacklistPort.blacklist(jti, ttl);

        return new LogoutResponse(true, "Logged out successfully");
    }
}