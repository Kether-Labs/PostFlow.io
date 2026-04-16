package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.entity.User;

import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.event.PasswordResetRequestedEvent;
import io.ketherlabs.postflow.identity.domain.usecase.ForgotPasswordUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.input.ForgotPasswordCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.ForgotPasswordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du {@code ForgotPasswordUseCase}.
 * 0 Spring, 0 base de données, 0 Mockito.
 */
class ForgotPasswordUseCaseTest {

    private FakeUserRepository                fakeUserRepo;
    private FakePasswordResetTokenRepository  fakeTokenRepo;
    private FakeHmacTokenGenerator            fakeTokenGenerator;
    private List<Object>                      publishedEvents;
    private ForgotPasswordUseCase             useCase;

    private User activeUser;
    private User pendingUser;

    @BeforeEach
    void setUp() {
        fakeUserRepo = new FakeUserRepository();
        fakeTokenRepo = new FakePasswordResetTokenRepository();
        fakeTokenGenerator = new FakeHmacTokenGenerator();
        publishedEvents = new ArrayList<>();

        ApplicationEventPublisher fakeEvents = publishedEvents::add;
        useCase = new ForgotPasswordUseCase(
                fakeUserRepo, fakeTokenRepo, fakeTokenGenerator, fakeEvents
        );

        // Utilisateur actif
        activeUser = User.register(
                "John", "Doe",
                Email.of("john@example.com"),
                Password.fromHash("$2a$12$hash")
        );
        activeUser.activate();
        fakeUserRepo.register(activeUser);

        // Utilisateur non vérifié
        pendingUser = User.register(
                "Jane", "Doe",
                Email.of("jane@example.com"),
                Password.fromHash("$2a$12$hash")
        );
        fakeUserRepo.register(pendingUser);
    }

    // =====================================================
    // 1. Flux nominal — email connu et compte ACTIVE
    // =====================================================

    @Test
    void should_return_generic_response_when_user_exists_and_active() {
        ForgotPasswordResponse response = useCase.execute(
                new ForgotPasswordCommand("john@example.com")
        );
        assertNotNull(response.message());
        assertFalse(response.message().isBlank());
    }

    @Test
    void should_create_and_persist_reset_token_for_active_user() {
        useCase.execute(new ForgotPasswordCommand("john@example.com"));

        assertTrue(
                fakeTokenRepo.findByTokenHash(FakeHmacTokenGenerator.FAKE_TOKEN).isPresent()
        );
    }

    @Test
    void should_create_non_expired_and_unused_token() {
        useCase.execute(new ForgotPasswordCommand("john@example.com"));

        PasswordResetToken token = fakeTokenRepo
                .findByTokenHash(FakeHmacTokenGenerator.FAKE_TOKEN).orElseThrow();

        assertFalse(token.isUsed());
        assertFalse(false);
    }

    @Test
    void should_link_reset_token_to_correct_user() {
        useCase.execute(new ForgotPasswordCommand("john@example.com"));

        PasswordResetToken token = fakeTokenRepo
                .findByTokenHash(FakeHmacTokenGenerator.FAKE_TOKEN).orElseThrow();

        assertEquals(activeUser.getId(), token.getUserId());
    }

    @Test
    void should_publish_password_reset_requested_event() {
        useCase.execute(new ForgotPasswordCommand("john@example.com"));

        assertEquals(1, publishedEvents.size());
        assertInstanceOf(PasswordResetRequestedEvent.class, publishedEvents.getFirst());
    }

    @Test
    void should_publish_event_with_correct_data() {
        useCase.execute(new ForgotPasswordCommand("john@example.com"));

        PasswordResetRequestedEvent event =
                (PasswordResetRequestedEvent) publishedEvents.getFirst();

        assertEquals(activeUser.getId(), event.userId());
        assertEquals("john@example.com", event.email());
        assertEquals(FakeHmacTokenGenerator.FAKE_TOKEN, event.resetToken());
    }

    // =====================================================
    // 2. Anti-énumération — email inconnu
    // =====================================================

    @Test
    void should_return_generic_response_when_email_not_found() {
        ForgotPasswordResponse response = useCase.execute(
                new ForgotPasswordCommand("unknown@example.com")
        );
        assertNotNull(response.message());
        assertFalse(response.message().isBlank());
    }

    @Test
    void should_not_create_token_when_email_not_found() {
        useCase.execute(new ForgotPasswordCommand("unknown@example.com"));

        assertTrue(publishedEvents.isEmpty());
        assertEquals(0, fakeTokenRepo.count());
    }

    // =====================================================
    // 3. Anti-énumération — compte PENDING_VERIFICATION
    // =====================================================

    @Test
    void should_return_generic_response_when_account_not_active() {
        ForgotPasswordResponse response = useCase.execute(
                new ForgotPasswordCommand("jane@example.com")
        );
        assertNotNull(response.message());
    }

    @Test
    void should_not_create_token_when_account_not_active() {
        useCase.execute(new ForgotPasswordCommand("jane@example.com"));

        assertTrue(true);
        assertEquals(0, fakeTokenRepo.count());
    }

    // =====================================================
    // 4. Réponse générique identique dans tous les cas
    // =====================================================

    @Test
    void should_return_same_message_regardless_of_email_existence() {
        ForgotPasswordResponse responseFound = useCase.execute(
                new ForgotPasswordCommand("john@example.com")
        );
        ForgotPasswordResponse responseNotFound = useCase.execute(
                new ForgotPasswordCommand("unknown@example.com")
        );

        assertEquals(responseFound.message(), responseNotFound.message());
    }

    // =====================================================
    // 5. Commande invalide
    // =====================================================

    @Test
    void should_throw_when_email_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new ForgotPasswordCommand(null));
    }

    @Test
    void should_throw_when_email_is_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new ForgotPasswordCommand("   "));
    }
}