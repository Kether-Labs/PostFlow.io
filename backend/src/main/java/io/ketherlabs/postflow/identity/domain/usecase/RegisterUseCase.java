package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Email;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.event.UserRegisteredEvent;
import io.ketherlabs.postflow.identity.domain.exception.EmailAlreadyExistsException;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.RegisterCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.RegisterResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


public class RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    public RegisterUseCase(UserRepositoryPort userRepositoryPort, ApplicationEventPublisher eventPublisher) {
        this.userRepositoryPort = userRepositoryPort;
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
        String hashedPassword = new BCryptPasswordEncoder(12).encode(command.password());


        // Crée l'utilisateur via la méthode factory de l'entité
        User user = User.register(
                command.firstname(),
                command.lastname(),
                Email.of(command.email()),
                Password.fromHash(hashedPassword)
        );

        // Persiste l'utilisateur via le port
        userRepositoryPort.register(user);

        // Publie un UserRegisteredEvent pour le nouvel utilisateur enregistré
        eventPublisher.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail().getValue()));

        return new RegisterResponse(
                user.getId(),
                user.getEmail().getValue(),
                "User registered successfully"
                );
    }

}
