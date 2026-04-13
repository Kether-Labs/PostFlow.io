package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.entity.RefreshToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.exception.TokenAlreadyUsedException;
import io.ketherlabs.postflow.identity.domain.exception.TokenExpiredException;
import io.ketherlabs.postflow.identity.domain.usecase.ResetPasswordUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.input.ResetPasswordCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.ResetPasswordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du {@code ResetPasswordUseCase}.
 * 0 Spring, 0 base de données, 0 Mockito.
 */
class ResetPasswordUseCaseTest {

    private FakeUserRepository               fakeUserRepo;
    private FakePasswordResetTokenRepository fakeTokenRepo;
    private FakeRefreshTokenRepository       fakeRefreshTokenRepo;
    private FakePasswordEncoderAdapter       fakeEncoder;
    private ResetPasswordUseCase             useCase;

    private User              activeUser;
    private PasswordResetToken validToken;
    private RefreshToken       activeRefreshToken;

    private static final String VALID_TOKEN    = "valid-reset-token";
    private static final String NEW_PASSWORD   = "newPassword123";
    private static final String CONFIRM_PASS   = "newPassword123";

    @BeforeEach
    void setUp() {
        fakeUserRepo         = new FakeUserRepository();
        fakeTokenRepo        = new FakePasswordResetTokenRepository();
        fakeRefreshTokenRepo = new FakeRefreshTokenRepository();
        fakeEncoder          = new FakePasswordEncoderAdapter();

        useCase = new ResetPasswordUseCase(
                fakeTokenRepo, fakeUserRepo,
                fakeRefreshTokenRepo, fakeEncoder
        );

        // Utilisateur actif
        activeUser = User.register(
                "John", "Doe",
                Email.of("john@example.com"),
                Password.fromHash("$2a$12$oldhash")
        );
        activeUser.activate();
        fakeUserRepo.register(activeUser);

        // Token valide non expiré non utilisé
        validToken = PasswordResetToken.create(VALID_TOKEN, activeUser.getId());
        fakeTokenRepo.save(validToken);

        // Refresh token actif pour cet utilisateur
        activeRefreshToken = RefreshToken.create(
                "refresh-hash-abc",
                Instant.now().plusSeconds(604800),
                activeUser.getId()

        );
        fakeRefreshTokenRepo.save(activeRefreshToken);
    }

    private ResetPasswordCommand validCommand() {
        return new ResetPasswordCommand(VALID_TOKEN, NEW_PASSWORD, CONFIRM_PASS);
    }

    // =====================================================
    // 1. Flux nominal
    // =====================================================

    @Test
    void should_return_success_response() {
        ResetPasswordResponse response = useCase.execute(validCommand());

        assertNotNull(response.message());
        assertFalse(response.message().isBlank());
    }

    @Test
    void should_update_user_password() {
        useCase.execute(validCommand());

        User updated = fakeUserRepo.findUserById(activeUser.getId());
        assertEquals("hashed_" + NEW_PASSWORD, updated.getPassword().getHashedValue());
    }

    @Test
    void should_not_store_plain_password() {
        useCase.execute(validCommand());

        User updated = fakeUserRepo.findUserById(activeUser.getId());
        assertNotEquals(NEW_PASSWORD, updated.getPassword().getHashedValue());
    }

    @Test
    void should_mark_token_as_used() {
        useCase.execute(validCommand());

        PasswordResetToken token = fakeTokenRepo
                .findByTokenHash(VALID_TOKEN).orElseThrow();
        assertTrue(token.isUsed());
    }

    @Test
    void should_revoke_all_refresh_tokens() {
        useCase.execute(validCommand());

        assertEquals(0, fakeRefreshTokenRepo.countActiveByUserId(activeUser.getId()));
    }

    @Test
    void should_revoke_only_tokens_of_concerned_user() {
        // Créer un refresh token pour un autre utilisateur
        UUID otherUserId = UUID.randomUUID();
        RefreshToken otherToken = RefreshToken.create(
                "other-hash", Instant.now().plusSeconds(604800),otherUserId

        );
        fakeRefreshTokenRepo.save(otherToken);

        useCase.execute(validCommand());

        // L'autre utilisateur garde son token actif
        assertEquals(1, fakeRefreshTokenRepo.countActiveByUserId(otherUserId));
    }

    // =====================================================
    // 2. Token invalide
    // =====================================================

    @Test
    void should_throw_invalid_token_when_token_not_found() {
        assertThrows(InvalidTokenException.class,
                () -> useCase.execute(
                        new ResetPasswordCommand("unknown-token", NEW_PASSWORD, CONFIRM_PASS)
                ));
    }

    @Test
    void should_throw_token_expired_when_token_expired() {
        PasswordResetToken expiredToken = PasswordResetToken.reconstruct(
                UUID.randomUUID(), "expired-token",
                activeUser.getId(),
                Instant.now().minusSeconds(1), // expiré
                LocalDateTime.now(),
                false

        );
        fakeTokenRepo.save(expiredToken);

        assertThrows(TokenExpiredException.class,
                () -> useCase.execute(
                        new ResetPasswordCommand("expired-token", NEW_PASSWORD, CONFIRM_PASS)
                ));
    }

    @Test
    void should_throw_token_already_used_when_token_used() {
        validToken.markAsUsed();
        fakeTokenRepo.save(validToken);

        assertThrows(TokenAlreadyUsedException.class,
                () -> useCase.execute(validCommand()));
    }

    // =====================================================
    // 3. Commande invalide
    // =====================================================

    @Test
    void should_throw_when_token_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResetPasswordCommand(null, NEW_PASSWORD, CONFIRM_PASS));
    }

    @Test
    void should_throw_when_password_too_short() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResetPasswordCommand(VALID_TOKEN, "short", "short"));
    }

    @Test
    void should_throw_when_passwords_do_not_match() {
        assertThrows(IllegalArgumentException.class,
                () -> new ResetPasswordCommand(VALID_TOKEN, NEW_PASSWORD, "different"));
    }

    @Test
    void should_throw_when_password_too_long() {
        String tooLong = "a".repeat(73);
        assertThrows(IllegalArgumentException.class,
                () -> new ResetPasswordCommand(VALID_TOKEN, tooLong, tooLong));
    }
}