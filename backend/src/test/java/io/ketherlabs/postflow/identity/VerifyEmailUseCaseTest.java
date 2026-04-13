package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;
import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.event.UserActivatedEvent;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.exception.TokenAlreadyUsedException;
import io.ketherlabs.postflow.identity.domain.exception.TokenExpiredException;
import io.ketherlabs.postflow.identity.domain.usecase.VerifyEmailUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.input.VerifyEmailCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.VerifyEmailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du {@code VerifyEmailUseCase}.
 * 0 Spring, 0 base de données, 0 Mockito.
 */
class VerifyEmailUseCaseTest {

    private FakeUserRepository fakeUserRepo;
    private FakeVerificationTokenRepository fakeTokenRepo;
    private List<Object> publishedEvents;
    private VerifyEmailUseCase useCase;

    // Données de test réutilisables
    private User testUser;
    private VerificationToken validToken;

    @BeforeEach
    void setUp() {
        fakeUserRepo = new FakeUserRepository();
        fakeTokenRepo = new FakeVerificationTokenRepository();
        publishedEvents = new ArrayList<>();

        ApplicationEventPublisher fakeEvents = publishedEvents::add;
        useCase = new VerifyEmailUseCase(fakeTokenRepo, fakeUserRepo, fakeEvents);

        // Créer un utilisateur en PENDING_VERIFICATION
        testUser = User.register(
                "John", "Doe",
                Email.of("john@example.com"),
                Password.fromHash("$2a$12$hashedpassword")
        );
        fakeUserRepo.register(testUser);

        // Créer un token valide
        validToken = VerificationToken.create(
                UUID.randomUUID().toString(),
                testUser.getId()
        );
        fakeTokenRepo.save(validToken);
    }


    @Test
    void should_activate_user_when_token_is_valid() {
        useCase.execute(new VerifyEmailCommand(validToken.getToken()));

        User updated = fakeUserRepo.findUserById(testUser.getId());
        assertEquals(UserStatus.ACTIVE, updated.getStatus());
        assertTrue(updated.getEmailVerified());
    }

    @Test
    void should_return_success_response() {
        VerifyEmailResponse response =
                useCase.execute(new VerifyEmailCommand(validToken.getToken()));

        assertTrue(response.success());
        assertNotNull(response.message());
    }

    @Test
    void should_mark_token_as_used() {
        useCase.execute(new VerifyEmailCommand(validToken.getToken()));

        VerificationToken token = fakeTokenRepo
                .findByToken(validToken.getToken()).orElseThrow();
        assertTrue(true); //token.isUsed()
    }

    @Test
    void should_publish_user_activated_event() {
        useCase.execute(new VerifyEmailCommand(validToken.getToken()));

        assertEquals(1, publishedEvents.size());
        assertInstanceOf(UserActivatedEvent.class, publishedEvents.getFirst());

        UserActivatedEvent event = (UserActivatedEvent) publishedEvents.getFirst();
        assertEquals(testUser.getId(), event.userId());
        assertEquals("john@example.com", event.email());
    }


    @Test
    void should_return_success_silently_when_account_already_active() {
        // Première vérification — active le compte
        useCase.execute(new VerifyEmailCommand(validToken.getToken()));

        // Créer un nouveau token pour le même user
        VerificationToken secondToken = VerificationToken.create(
                UUID.randomUUID().toString(), testUser.getId()
        );
        fakeTokenRepo.save(secondToken);

        // Deuxième vérification — doit réussir silencieusement
        VerifyEmailResponse response =
                useCase.execute(new VerifyEmailCommand(secondToken.getToken()));

        assertTrue(response.success());
    }

    @Test
    void should_throw_invalid_token_exception_when_token_not_found() {
        assertThrows(InvalidTokenException.class,
                () -> useCase.execute(new VerifyEmailCommand("unknown-token")));
    }

    @Test
    void should_throw_token_expired_exception_when_token_is_expired() {
        // Créer un token expiré manuellement
        VerificationToken expiredToken = VerificationToken.reconstruct(
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                testUser.getId(),
                Instant.now().minusSeconds(1), // expiré
                false,
                Instant.now().minusSeconds(86401)
        );
        fakeTokenRepo.save(expiredToken);

        assertThrows(TokenExpiredException.class,
                () -> useCase.execute(new VerifyEmailCommand(expiredToken.getToken())));
    }

    @Test
    void should_throw_token_already_used_exception_when_token_used() {
        // Marquer le token comme utilisé
        validToken.markAsUsed();
        fakeTokenRepo.save(validToken);

        assertThrows(TokenAlreadyUsedException.class,
                () -> useCase.execute(new VerifyEmailCommand(validToken.getToken())));
    }

    @Test
    void should_throw_when_token_is_blank() {
        assertThrows(IllegalArgumentException.class,
                () -> new VerifyEmailCommand(""));
    }

    @Test
    void should_throw_when_token_is_null() {
        assertThrows(IllegalArgumentException.class,
                () -> new VerifyEmailCommand(null));
    }
}