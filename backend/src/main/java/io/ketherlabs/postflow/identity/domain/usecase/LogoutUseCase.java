package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import io.ketherlabs.postflow.identity.domain.port.PasswordEncoderPort;
import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.LogoutCommand;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case UC-AUTH-005 : Déconnexion d'un utilisateur.
 *
 * <p>Flux principal :
 * <ol>
 *   <li>Extraire le {@code jti} et le TTL résiduel de l'Access Token JWT via {@code JwtTokenPort}</li>
 *   <li>Blacklister le {@code jti} dans Redis avec le TTL résiduel pour invalider l'Access Token avant son expiration naturelle</li>
 *   <li>Hasher le Refresh Token reçu et le rechercher en base</li>
 *   <li>Si le Refresh Token existe, le révoquer (la session courante est fermée)</li>
 * </ol>
 *
 * <p>Ne révoque que le Refresh Token de la session courante — les autres sessions
 * actives de l'utilisateur (autres appareils) restent valides.
 */
public class LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtTokenPort               jwtTokenPort;
    private final RedisBlacklistPort         redisBlacklistPort;
    private final PasswordEncoderPort        passwordEncoder;

    public LogoutUseCase(RefreshTokenRepositoryPort refreshTokenRepository,
                         JwtTokenPort jwtTokenPort,
                         RedisBlacklistPort redisBlacklistPort,
                         PasswordEncoderPort passwordEncoder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenPort           = jwtTokenPort;
        this.redisBlacklistPort     = redisBlacklistPort;
        this.passwordEncoder        = passwordEncoder;
    }

    /**
     * Exécute la déconnexion d'un utilisateur.
     *
     * @param command la commande contenant l'Access Token et le Refresh Token de la session courante
     * @throws IllegalArgumentException si l'Access Token ou le Refresh Token est null ou vide (validé par {@link LogoutCommand})
     */
    @Transactional
    public void execute(LogoutCommand command) {

        // 1. Extraire le jti de l'Access Token
        String jti = jwtTokenPort.extractJti(command.accessToken());

        // 2. Extraire le TTL résiduel de l'Access Token
        long ttlSeconds = jwtTokenPort.getRemainingTtlSeconds(command.accessToken());

        // 3. Blacklister le jti dans Redis avec le TTL résiduel
        redisBlacklistPort.blacklist(jti, ttlSeconds);

        // 4. Hasher le Refresh Token et le révoquer s'il existe en base
        String refreshTokenHash = passwordEncoder.encode(command.refreshToken());
        refreshTokenRepository.findByTokenHash(refreshTokenHash)
                .ifPresent(refreshTokenRepository::revoke);
    }

}
