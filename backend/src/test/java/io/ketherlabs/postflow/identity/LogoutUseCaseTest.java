package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.usecase.LogoutUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.input.LogoutCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du UseCase {@link LogoutUseCase}.
 * Valide l'ensemble du processus de deconnexion :
 * blacklist de l'access token dans Redis et revocation du refresh token de la session courante.
 */
class LogoutUseCaseTest {

    private FakeUserRepository fakeUserRepo;
    private FakeJwtTokenAdapter fakeJwtTokenAdapter;
    private FakeRefreshTokenRepository fakeRefreshTokenRepo;
    private FakeRefreshTokenAdapter fakeRefreshTokenAdapter;
    private FakePasswordEncoderAdapter fakePasswordEncoder;
    private FakeRedisBlackListAdapter fakeRedisBlackList;
    private LogoutUseCase useCase;

    @BeforeEach
    void setUp() {
        fakeUserRepo = new FakeUserRepository();
        fakeJwtTokenAdapter = new FakeJwtTokenAdapter();
        fakeRefreshTokenRepo = new FakeRefreshTokenRepository();
        fakeRefreshTokenAdapter = new FakeRefreshTokenAdapter();
        fakePasswordEncoder = new FakePasswordEncoderAdapter();
        fakeRedisBlackList = new FakeRedisBlackListAdapter();
        useCase = new LogoutUseCase(
                fakeRefreshTokenRepo,
                fakeJwtTokenAdapter,
                fakeRedisBlackList,
                fakePasswordEncoder
        );
    }

    /**
     * Cree un utilisateur ACTIVE et enregistre son refresh token dans le repository.
     * Retourne une session prete a deconnecter (accessToken + refreshToken).
     */
    private Session createActiveSession() {
        User user = User.register(
                "John",
                "Doe",
                Email.of("john@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("securePassword123"))
        );
        user.activate();
        fakeUserRepo.register(user);

        String accessToken = fakeJwtTokenAdapter.generateAccessToken(user);
        String refreshToken = fakeRefreshTokenAdapter.generateRefreshToken(user);

        String refreshTokenHash = fakePasswordEncoder.encode(refreshToken);
        RefreshToken refreshTokenEntity = RefreshToken.create(
                refreshTokenHash,
                Instant.now().plusSeconds(7 * 24 * 3600),
                user.getId()
        );
        fakeRefreshTokenRepo.save(refreshTokenEntity);

        return new Session(user, accessToken, refreshToken, refreshTokenHash);
    }

    private record Session(User user, String accessToken, String refreshToken, String refreshTokenHash) {}

    // =====================================================
    // 1. Test du happy path : blacklist + revocation
    // =====================================================

    @Test
    void should_blacklist_access_token_jti() {
        Session session = createActiveSession();
        String jti = fakeJwtTokenAdapter.extractJti(session.accessToken());

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        assertTrue(fakeRedisBlackList.isBlacklisted(jti));
    }

    @Test
    void should_blacklist_with_remaining_ttl_of_access_token() {
        Session session = createActiveSession();
        String jti = fakeJwtTokenAdapter.extractJti(session.accessToken());
        long expectedTtl = fakeJwtTokenAdapter.getRemainingTtlSeconds(session.accessToken());

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        assertEquals(expectedTtl, fakeRedisBlackList.getTtlSeconds(jti));
    }

    @Test
    void should_revoke_refresh_token() {
        Session session = createActiveSession();

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        RefreshToken stored = fakeRefreshTokenRepo.findByTokenHash(session.refreshTokenHash()).orElseThrow();
        assertTrue(stored.isRevoked());
    }

    @Test
    void should_decrease_active_refresh_token_count_after_logout() {
        Session session = createActiveSession();
        assertEquals(1, fakeRefreshTokenRepo.countActiveByUserId(session.user().getId()));

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        assertEquals(0, fakeRefreshTokenRepo.countActiveByUserId(session.user().getId()));
    }

    // =====================================================
    // 2. Test d'isolation : autres sessions non affectees
    // =====================================================

    @Test
    void should_not_revoke_refresh_tokens_of_other_users() {
        Session session = createActiveSession();

        User otherUser = User.register(
                "Jane",
                "Doe",
                Email.of("jane@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("otherPassword123"))
        );
        otherUser.activate();
        fakeUserRepo.register(otherUser);
        String otherRefreshToken = fakeRefreshTokenAdapter.generateRefreshToken(otherUser);
        RefreshToken otherEntity = RefreshToken.create(
                fakePasswordEncoder.encode(otherRefreshToken),
                Instant.now().plusSeconds(7 * 24 * 3600),
                otherUser.getId()
        );
        fakeRefreshTokenRepo.save(otherEntity);

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        assertEquals(1, fakeRefreshTokenRepo.countActiveByUserId(otherUser.getId()));
    }

    @Test
    void should_not_blacklist_other_users_jti() {
        Session session = createActiveSession();

        User otherUser = User.register(
                "Jane",
                "Doe",
                Email.of("jane@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("otherPassword123"))
        );
        otherUser.activate();
        fakeUserRepo.register(otherUser);
        String otherAccessToken = fakeJwtTokenAdapter.generateAccessToken(otherUser);
        String otherJti = fakeJwtTokenAdapter.extractJti(otherAccessToken);

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        assertFalse(fakeRedisBlackList.isBlacklisted(otherJti));
    }

    // =====================================================
    // 3. Test d'Edge case : refresh token inconnu
    // =====================================================

    @Test
    void should_not_throw_when_refresh_token_is_not_in_repository() {
        User user = User.register(
                "John",
                "Doe",
                Email.of("john@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("securePassword123"))
        );
        user.activate();
        fakeUserRepo.register(user);

        String accessToken = fakeJwtTokenAdapter.generateAccessToken(user);
        String unknownRefreshToken = fakeRefreshTokenAdapter.generateRefreshToken(user);
        // Aucune entite RefreshToken persistee

        assertDoesNotThrow(() -> useCase.execute(new LogoutCommand(accessToken, unknownRefreshToken)));
    }

    @Test
    void should_still_blacklist_access_token_when_refresh_token_is_unknown() {
        User user = User.register(
                "John",
                "Doe",
                Email.of("john@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("securePassword123"))
        );
        user.activate();
        fakeUserRepo.register(user);

        String accessToken = fakeJwtTokenAdapter.generateAccessToken(user);
        String jti = fakeJwtTokenAdapter.extractJti(accessToken);
        String unknownRefreshToken = fakeRefreshTokenAdapter.generateRefreshToken(user);

        useCase.execute(new LogoutCommand(accessToken, unknownRefreshToken));

        assertTrue(fakeRedisBlackList.isBlacklisted(jti));
    }

    // =====================================================
    // 4. Test d'exception : access token inconnu
    // =====================================================

    @Test
    void should_throw_when_access_token_is_unknown() {
        Session session = createActiveSession();

        LogoutCommand command = new LogoutCommand("unknown-access-token", session.refreshToken());

        assertThrows(
                InvalidTokenException.class,
                () -> useCase.execute(command)
        );
    }

    // =====================================================
    // 5. Test de validation de la commande LogoutCommand
    // =====================================================

    @Test
    void should_throw_when_access_token_is_null() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LogoutCommand(null, "some-refresh-token")
        );
    }

    @Test
    void should_throw_when_access_token_is_blank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LogoutCommand("  ", "some-refresh-token")
        );
    }

    @Test
    void should_throw_when_refresh_token_is_null() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LogoutCommand("some-access-token", null)
        );
    }

    @Test
    void should_throw_when_refresh_token_is_blank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new LogoutCommand("some-access-token", "  ")
        );
    }

    // =====================================================
    // 6. Test idempotence : re-logout apres revocation
    // =====================================================

    @Test
    void should_throw_when_refresh_token_is_already_revoked() {
        Session session = createActiveSession();

        useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()));

        // Un second logout doit échouer car RefreshToken.revoke() refuse la double revocation
        assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(new LogoutCommand(session.accessToken(), session.refreshToken()))
        );
    }
}
