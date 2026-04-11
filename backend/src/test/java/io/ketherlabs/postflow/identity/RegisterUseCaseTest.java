package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.event.UserRegisteredEvent;
import io.ketherlabs.postflow.identity.domain.exception.EmailAlreadyExistsException;
import io.ketherlabs.postflow.identity.domain.usecase.RegisterUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.input.RegisterCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests unitaires du UseCase {@link RegisterUseCase} utilisateur.
 * Valide l'ensemble du processus d'enregistrement : validation,
 * hashage du mot de passe, persistance et publication d'événements.
 */

class RegisterUseCaseTest {

    private FakeUserRepository fakeRepo;
    private List<Object> publishedEvents;
    private RegisterUseCase useCase;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeUserRepository();
        publishedEvents = new ArrayList<>();
        ApplicationEventPublisher eventsPublisher = publishedEvents::add;
        useCase = new RegisterUseCase(fakeRepo, eventsPublisher);
    }

    private RegisterCommand validCommand() {
        return new RegisterCommand("John", "Doe", "john@example.com", "securePassword123");
    }


    // =====================================================
    // 1. Test de réponse : id, email et message de confirmation
    // =====================================================

    @Test
    void should_return_response_with_user_id_email_and_message() {
        RegisterResponse response = useCase.execute(validCommand());

        assertNotNull(response.userId());
        assertEquals("john@example.com", response.email());
        assertEquals("User registered successfully", response.message());
    }

    // =====================================================
    // 2. Test d'exception : email déjà existant
    // =====================================================

    @Test
    void should_throw_exception_when_email_already_exists() {
        useCase.execute(validCommand());

        EmailAlreadyExistsException ex = assertThrows(
                EmailAlreadyExistsException.class,
                () -> useCase.execute(validCommand())
        );
        assertTrue(ex.getMessage().contains("john@example.com"));
    }

    @Test
    void should_not_persist_duplicate_when_email_already_exists() {
        useCase.execute(validCommand());

        assertThrows(EmailAlreadyExistsException.class, () -> useCase.execute(
                new RegisterCommand("Jane", "Anne", "john@example.com", "otherPass123")
        ));

        // L'utilisateur persisté reste le premier (John), aucun doublon
        User persisted = fakeRepo.findByEmail("john@example.com").orElseThrow();
        assertEquals("John", persisted.getFirstname());
    }

    // =====================================================
    // 3. Test du hashage du mot de passe
    // =====================================================

    @Test
    void should_hash_password_with_bcrypt() {
        useCase.execute(validCommand());

        User persisted = fakeRepo.findByEmail("john@example.com").orElseThrow();
        String hash = persisted.getPassword().getHashedValue();

        assertNotEquals("securePassword123", hash);
        assertTrue(hash.startsWith("$2a$12$"));
    }

    @Test
    void should_use_bcrypt_cost_factor_12() {
        useCase.execute(validCommand());

        User persisted = fakeRepo.findByEmail("john@example.com").orElseThrow();
        String hash = persisted.getPassword().getHashedValue();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        assertTrue(encoder.matches("securePassword123", hash));
    }

    // =====================================================
    // 4. Test de création de l'utilisateur
    // =====================================================

    @Test
    void should_create_user_with_correct_fields() {
        useCase.execute(validCommand());

        User persisted = fakeRepo.findByEmail("john@example.com").orElseThrow();

        assertNotNull(persisted.getId());
        assertEquals("John", persisted.getFirstname());
        assertEquals("Doe", persisted.getLastname());
        assertEquals("john@example.com", persisted.getEmail().getValue());
        assertNotEquals("securePassword123", persisted.getPassword().getHashedValue());
        assertEquals(UserStatus.PENDING_VERIFICATION, persisted.getStatus());
        assertFalse(persisted.getEmailVerified());
        assertNotNull(persisted.getCreatedAt());
        assertNull(persisted.getLastLoginAt());
    }

    // =====================================================
    // 5. Test de persistance
    // =====================================================

    @Test
    void should_persist_user_via_repository() {
        useCase.execute(validCommand());

        assertTrue(fakeRepo.existsByEmail("john@example.com"));
    }

    @Test
    void should_persist_the_created_user() {
        RegisterResponse response = useCase.execute(validCommand());

        User persisted = fakeRepo.findUserById(response.userId());

        assertNotNull(persisted);
        assertEquals(response.userId(), persisted.getId());
        assertEquals(response.email(), persisted.getEmail().getValue());
    }

    // =====================================================
    // 6. Test de publication d'événement
    // =====================================================

    @Test
    void should_publish_user_registered_event() {
        useCase.execute(validCommand());

        assertEquals(1, publishedEvents.size());
        assertInstanceOf(UserRegisteredEvent.class, publishedEvents.getFirst());
    }

    @Test
    void should_publish_event_with_correct_id_and_email() {
        RegisterResponse response = useCase.execute(validCommand());

        UserRegisteredEvent event = (UserRegisteredEvent) publishedEvents.getFirst();

        assertEquals(response.userId(), event.userid());
        assertEquals("john@example.com", event.email());
    }

}
