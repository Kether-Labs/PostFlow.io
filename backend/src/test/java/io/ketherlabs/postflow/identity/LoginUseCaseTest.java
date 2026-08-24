package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.exception.AccountNotVerifiedException;
import io.ketherlabs.postflow.identity.domain.exception.AccountSuspendedException;
import io.ketherlabs.postflow.identity.domain.exception.InvalidCredentialsException;
import io.ketherlabs.postflow.identity.domain.usecase.LoginUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.input.LoginCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests unitaires du UseCase {@link LoginUseCase}.
 * Valide l'ensemble du processus de connexion : authentification,
 * generation des tokens, persistance du refresh token et mise a jour du lastLogin.
 */

class LoginUseCaseTest {

    private FakeUserRepository fakeUserRepo;
    private FakeJwtTokenAdapter fakeJwtTokenAdapter;
    private FakeRefreshTokenRepository fakeRefreshTokenRepo;
    private FakeRefreshTokenAdapter fakeRefreshTokenAdapter;
    private FakePasswordEncoderAdapter fakePasswordEncoder;
    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        fakeUserRepo = new FakeUserRepository();
        fakeJwtTokenAdapter = new FakeJwtTokenAdapter();
        fakeRefreshTokenRepo = new FakeRefreshTokenRepository();
        fakeRefreshTokenAdapter = new FakeRefreshTokenAdapter();
        fakePasswordEncoder = new FakePasswordEncoderAdapter();
        useCase = new LoginUseCase(
                fakeUserRepo,
                fakeJwtTokenAdapter,
                fakeRefreshTokenRepo,
                fakeRefreshTokenAdapter,
                fakePasswordEncoder
        );
    }

    private LoginCommand validCommand() {
        return new LoginCommand("john@example.com", "securePassword123");
    }

    /**
     * Cree un utilisateur ACTIVE avec le mot de passe hashe via le FakePasswordEncoder.
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
     * Cree un utilisateur PENDING_VERIFICATION (non active).
     */
    private void registerPendingUser() {
        User user = User.register(
                "John",
                "Doe",
                Email.of("john@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("securePassword123"))
        );
        fakeUserRepo.register(user);
    }

    /**
     * Cree un utilisateur SUSPENDED.
     */
    private void registerSuspendedUser() {
        User user = User.register(
                "John",
                "Doe",
                Email.of("john@example.com"),
                Password.fromHash(fakePasswordEncoder.encode("securePassword123"))
        );
        user.activate();
        user.suspend();
        fakeUserRepo.register(user);
    }

    // =====================================================
    // 1. Test de reponse : access token et refresh token
    // =====================================================

    @Test
    void should_return_response_with_access_and_refresh_tokens() {
        registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertFalse(response.accessToken().isBlank());
        assertFalse(response.refreshToken().isBlank());
    }

    // =====================================================
    // 2. Test d'exception : email inconnu
    // =====================================================

    @Test
    void should_throw_invalid_credentials_when_email_not_found() {
        // Aucun utilisateur enregistre

        assertThrows(
                InvalidCredentialsException.class,
                () -> useCase.execute(validCommand())
        );
    }

    @Test
    void should_throw_invalid_credentials_with_correct_message_when_email_not_found() {
        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> useCase.execute(validCommand())
        );
        assertTrue(ex.getMessage().contains("Invalid email or password"));
    }

    // =====================================================
    // 3. Test d'exception : compte non verifie
    // =====================================================

    @Test
    void should_throw_exception_when_account_is_pending_verification() {
        registerPendingUser();

        assertThrows(
                AccountNotVerifiedException.class,
                () -> useCase.execute(validCommand())
        );
    }

    // =====================================================
    // 4. Test d'exception : compte suspendu
    // =====================================================

    @Test
    void should_throw_exception_when_account_is_suspended() {
        registerSuspendedUser();

        assertThrows(
                AccountSuspendedException.class,
                () -> useCase.execute(validCommand())
        );
    }

    // =====================================================
    // 5. Test d'exception : mot de passe incorrect
    // =====================================================

    @Test
    void should_throw_invalid_credentials_when_password_is_wrong() {
        registerAndActivateUser();

        LoginCommand wrongPasswordCmd = new LoginCommand("john@example.com", "wrongPassword123");

        assertThrows(
                InvalidCredentialsException.class,
                () -> useCase.execute(wrongPasswordCmd)
        );
    }

    @Test
    void should_not_generate_tokens_when_password_is_wrong() {
        registerAndActivateUser();

        LoginCommand wrongPasswordCmd = new LoginCommand("john@example.com", "wrongPassword123");

        assertThrows(InvalidCredentialsException.class, () -> useCase.execute(wrongPasswordCmd));

        // Aucun refresh token ne doit etre persiste
        User user = fakeUserRepo.findByEmail("john@example.com").orElseThrow();
        assertEquals(0, fakeRefreshTokenRepo.countActiveByUserId(user.getId()));
    }

    // =====================================================
    // 6. Test de generation du JWT access token
    // =====================================================

    @Test
    void should_generate_access_token_for_user() {
        registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        assertTrue(response.accessToken().startsWith("fake-access-token-"));
    }

    @Test
    void should_generate_valid_access_token() {
        registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        assertTrue(fakeJwtTokenAdapter.isValid(response.accessToken()));
    }

    // =====================================================
    // 7. Test de generation et persistance du refresh token
    // =====================================================

    @Test
    void should_generate_refresh_token() {
        registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        assertNotNull(response.refreshToken());
        assertTrue(response.refreshToken().startsWith("fake-refresh-token-for-user-"));
    }

    @Test
    void should_persist_refresh_token_in_repository() {
        User user = registerAndActivateUser();

        useCase.execute(validCommand());

        assertEquals(1, fakeRefreshTokenRepo.countActiveByUserId(user.getId()));
    }

    @Test
    void should_persist_hashed_refresh_token() {
        registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        // Le token persisté est le hash du raw token, pas le raw token lui-meme
        String expectedHash = fakePasswordEncoder.encode(response.refreshToken());
        assertTrue(fakeRefreshTokenRepo.findByTokenHash(expectedHash).isPresent());
    }

    @Test
    void should_create_non_revoked_refresh_token() {
        registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        String tokenHash = fakePasswordEncoder.encode(response.refreshToken());
        RefreshToken refreshToken = fakeRefreshTokenRepo.findByTokenHash(tokenHash).orElseThrow();
        assertFalse(refreshToken.isRevoked());
        assertTrue(refreshToken.isValid());
    }

    @Test
    void should_link_refresh_token_to_correct_user() {
        User user = registerAndActivateUser();

        LoginResponse response = useCase.execute(validCommand());

        String tokenHash = fakePasswordEncoder.encode(response.refreshToken());
        RefreshToken refreshToken = fakeRefreshTokenRepo.findByTokenHash(tokenHash).orElseThrow();
        assertEquals(user.getId(), refreshToken.getUserId());
    }

    // =====================================================
    // 8. Test de mise a jour de la derniere connexion
    // =====================================================

    @Test
    void should_update_last_login_date() {
        User user = registerAndActivateUser();
        assertNull(user.getLastLoginAt());

        useCase.execute(validCommand());

        User updated = fakeUserRepo.findByEmail("john@example.com").orElseThrow();
        assertNotNull(updated.getLastLoginAt());
    }

    // =====================================================
    // 9. Test de validation de la commande LoginCommand
    // =====================================================

    @Test
    void should_throw_when_email_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand(null, "password123"));
    }

    @Test
    void should_throw_when_email_is_blank() {
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("  ", "password123"));
    }

    @Test
    void should_throw_when_email_format_is_invalid() {
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("invalid-email", "password123"));
    }

    @Test
    void should_throw_when_password_is_null() {
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("john@example.com", null));
    }

    @Test
    void should_throw_when_password_is_blank() {
        assertThrows(IllegalArgumentException.class, () -> new LoginCommand("john@example.com", "  "));
    }
}
