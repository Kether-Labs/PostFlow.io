package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.exception.InvalidRefreshTokenException;
import io.ketherlabs.postflow.identity.domain.exception.RefreshTokenExpiredException;
import io.ketherlabs.postflow.identity.domain.exception.RefreshTokenRevokedException;
import io.ketherlabs.postflow.identity.domain.usecase.RefreshTokenUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.output.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests unitaires du UseCase {@link RefreshTokenUseCase}.
 * Valide l'ensemble du processus de rotation : validation du token,
 * verification TTL/revocation, revocation de l'ancien token,
 * generation et persistance d'un nouveau couple access + refresh token.
 */

class RefreshTokenUseCaseTest {

    private FakeUserRepository fakeUserRepo;
    private FakeJwtTokenAdapter fakeJwtTokenAdapter;
    private FakeRefreshTokenRepository fakeRefreshTokenRepo;
    private FakeRefreshTokenAdapter fakeRefreshTokenAdapter;
    private FakePasswordEncoderAdapter fakePasswordEncoder;
    private RefreshTokenUseCase useCase;

    @BeforeEach
    void setUp() {
        fakeUserRepo = new FakeUserRepository();
        fakeJwtTokenAdapter = new FakeJwtTokenAdapter();
        fakeRefreshTokenRepo = new FakeRefreshTokenRepository();
        fakeRefreshTokenAdapter = new FakeRefreshTokenAdapter();
        fakePasswordEncoder = new FakePasswordEncoderAdapter();
        useCase = new RefreshTokenUseCase(
                fakeRefreshTokenRepo,
                fakeJwtTokenAdapter,
                fakeUserRepo,
                fakeRefreshTokenAdapter,
                fakePasswordEncoder
        );
    }

    /**
     * Cree un utilisateur ACTIVE et le persiste.
     */
    private User registerAndActivateUser() {
        User user = User.register(
                "John",
                "Doe",
                Email.of("john@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("securePassword123"))
        );
        user.activate();
        fakeUserRepo.register(user);
        return user;
    }

    /**
     * Persiste un refresh token hashé pour un utilisateur avec un TTL donne.
     */
    private RefreshToken persistRefreshToken(User user, String rawToken, Instant expiresAt) {
        RefreshToken token = RefreshToken.create(
                fakePasswordEncoder.encode(rawToken),
                expiresAt,
                user.getId()
        );
        fakeRefreshTokenRepo.save(token);
        return token;
    }

    /**
     * Persiste un refresh token revoque pour un utilisateur.
     */
    private void persistRevokedRefreshToken(User user, String rawToken) {
        RefreshToken token = persistRefreshToken(user, rawToken, Instant.now().plusSeconds(3600));
        token.markAsRevoke();
    }

    // =====================================================
    // 1. Test de reponse : nouveau access token et nouveau refresh token
    // =====================================================

    @Test
    void should_return_response_with_new_access_and_refresh_tokens() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertFalse(response.accessToken().isBlank());
        assertFalse(response.refreshToken().isBlank());
    }

    // =====================================================
    // 2. Test de validation : token null ou vide
    // =====================================================

    @Test
    void should_throw_illegal_argument_when_token_is_null() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(null));
    }

    @Test
    void should_throw_illegal_argument_when_token_is_blank() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("  "));
    }

    @Test
    void should_throw_illegal_argument_when_token_is_empty() {
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(""));
    }

    // =====================================================
    // 3. Test d'exception : token introuvable
    // =====================================================

    @Test
    void should_throw_invalid_refresh_token_when_token_not_found() {
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> useCase.execute("unknown-token")
        );
    }

    @Test
    void should_throw_invalid_refresh_token_with_correct_message_when_token_not_found() {
        InvalidRefreshTokenException ex = assertThrows(
                InvalidRefreshTokenException.class,
                () -> useCase.execute("unknown-token")
        );
        assertTrue(ex.getMessage().contains("Invalid refresh token:"));
    }

    // =====================================================
    // 4. Test d'exception : token expiré
    // =====================================================

    @Test
    void should_throw_expired_exception_when_token_is_expired() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-expired-token", Instant.now().minusSeconds(1));

        assertThrows(
                RefreshTokenExpiredException.class,
                () -> useCase.execute("raw-expired-token")
        );
    }

    // =====================================================
    // 5. Test d'exception : token deja révoqué
    // =====================================================

    @Test
    void should_throw_revoked_exception_when_token_is_revoked() {
        User user = registerAndActivateUser();
        persistRevokedRefreshToken(user, "raw-revoked-token");

        assertThrows(
                RefreshTokenRevokedException.class,
                () -> useCase.execute("raw-revoked-token")
        );
    }

    // =====================================================
    // 6. Test d'exception : utilisateur introuvable
    // =====================================================

    @Test
    void should_throw_invalid_refresh_token_when_user_not_found() {
        // Token persiste mais l'utilisateur associe n'existe pas
        RefreshToken orphanToken = RefreshToken.create(
                fakePasswordEncoder.encode("raw-orphan-token"),
                Instant.now().plusSeconds(3600),
                UUID.randomUUID()
        );
        fakeRefreshTokenRepo.save(orphanToken);

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> useCase.execute("raw-orphan-token")
        );
    }

    // =====================================================
    // 7. Test de rotation : ancien token révoqué
    // =====================================================

    @Test
    void should_revoke_old_refresh_token_after_rotation() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        useCase.execute("raw-old-token");

        String oldHash = fakePasswordEncoder.encode("raw-old-token");
        RefreshToken oldToken = fakeRefreshTokenRepo.findByTokenHash(oldHash).orElseThrow();
        assertTrue(oldToken.isRevoked());
    }

    // =====================================================
    // 8. Test de generation du nouveau JWT access token
    // =====================================================

    @Test
    void should_generate_new_access_token() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        assertTrue(response.accessToken().startsWith("fake-access-token-"));
    }

    @Test
    void should_generate_valid_new_access_token() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        assertTrue(fakeJwtTokenAdapter.isValid(response.accessToken()));
    }

    // =====================================================
    // 9. Test de generation et persistance du nouveau refresh token
    // =====================================================

    @Test
    void should_generate_new_refresh_token_distinct_from_old() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        assertNotEquals("raw-old-token", response.refreshToken());
        assertTrue(response.refreshToken().startsWith("fake-refresh-token-for-user-"));
    }

    @Test
    void should_persist_new_hashed_refresh_token() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        // Le token persiste est le hash du raw token retourné, pas le raw token lui-meme
        String expectedHash = fakePasswordEncoder.encode(response.refreshToken());
        assertTrue(fakeRefreshTokenRepo.findByTokenHash(expectedHash).isPresent());
    }

    @Test
    void should_create_non_revoked_new_refresh_token() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        String newHash = fakePasswordEncoder.encode(response.refreshToken());
        RefreshToken newToken = fakeRefreshTokenRepo.findByTokenHash(newHash).orElseThrow();
        assertFalse(newToken.isRevoked());
        assertTrue(newToken.isValid());
    }

    @Test
    void should_link_new_refresh_token_to_correct_user() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-old-token", Instant.now().plusSeconds(3600));

        LoginResponse response = useCase.execute("raw-old-token");

        String newHash = fakePasswordEncoder.encode(response.refreshToken());
        RefreshToken newToken = fakeRefreshTokenRepo.findByTokenHash(newHash).orElseThrow();
        assertEquals(user.getId(), newToken.getUserId());
    }

    // =====================================================
    // 10. Test : pas de nouveau token persiste sur chemin d'erreur
    // =====================================================

    @Test
    void should_not_persist_new_token_when_token_not_found() {
        User user = registerAndActivateUser();

        assertThrows(InvalidRefreshTokenException.class, () -> useCase.execute("unknown-token"));

        assertEquals(0, fakeRefreshTokenRepo.countActiveByUserId(user.getId()));
    }

    @Test
    void should_not_persist_new_token_when_existing_is_expired() {
        User user = registerAndActivateUser();
        persistRefreshToken(user, "raw-expired-token", Instant.now().minusSeconds(1));

        assertThrows(RefreshTokenExpiredException.class, () -> useCase.execute("raw-expired-token"));

        // Hash du nouveau token qui aurait ete genere — ne doit pas exister en base
        String newRawToken = "fake-refresh-token-for-user-" + user.getId();
        String newHash = fakePasswordEncoder.encode(newRawToken);
        assertTrue(fakeRefreshTokenRepo.findByTokenHash(newHash).isEmpty());
    }

    @Test
    void should_not_persist_new_token_when_existing_is_revoked() {
        User user = registerAndActivateUser();
        persistRevokedRefreshToken(user, "raw-revoked-token");

        assertThrows(RefreshTokenRevokedException.class, () -> useCase.execute("raw-revoked-token"));

        String newRawToken = "fake-refresh-token-for-user-" + user.getId();
        String newHash = fakePasswordEncoder.encode(newRawToken);
        assertTrue(fakeRefreshTokenRepo.findByTokenHash(newHash).isEmpty());
    }
}
