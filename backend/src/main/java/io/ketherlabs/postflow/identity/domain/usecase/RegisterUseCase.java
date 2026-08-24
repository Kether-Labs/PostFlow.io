package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.event.UserRegisteredEvent;
import io.ketherlabs.postflow.identity.domain.exception.EmailAlreadyExistsException;
import io.ketherlabs.postflow.identity.domain.port.PasswordEncoderPort;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.VerificationTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.RegisterCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.RegisterResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


public class RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final VerificationTokenRepositoryPort tokenRepositoryPort;
    private final PasswordEncoderPort passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public RegisterUseCase(UserRepositoryPort userRepositoryPort,
                           VerificationTokenRepositoryPort tokenRepositoryPort,
                           PasswordEncoderPort passwordEncoder,
                           ApplicationEventPublisher eventPublisher) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenRepositoryPort = tokenRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    /**
     * UseCase pour l'inscription d'un nouvel utilisateur
     * @param command la commande contenant les informations d'inscription (firstname, lastname, email, password)
     * @return une réponse contenant l'id, l'email et un message de confirmation
     * @throws EmailAlreadyExistsException si l'email existe deja
     */
    @Transactional
    public RegisterResponse execute(RegisterCommand command) {

        // Vérifie si l'email est déjà utilisé
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        // Hashage du mot de passe avec BCrypt cost factor 12
        String hashedPassword = passwordEncoder.encode(command.password());


        // Crée l'utilisateur via la méthode factory de l'entité
        User user = User.register(
                command.firstname(),
                command.lastname(),
                Email.of(command.email()),
                Password.fromHash(hashedPassword)
        );

        // Persiste l'utilisateur via le port
        userRepositoryPort.register(user);

        // creation du token de verification lors de l'inscription
        VerificationToken token = VerificationToken.create(
                UUID.randomUUID().toString(),
                user.getId()
        );
        tokenRepositoryPort.save(token);

        // Publie un UserRegisteredEvent pour le nouvel utilisateur enregistré
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail().getValue(), token.getToken()));

        return new RegisterResponse(
                user.getId(),
                user.getEmail().getValue(),
                "User registered successfully"
                );
    }

}
