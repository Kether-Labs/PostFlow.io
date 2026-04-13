package io.ketherlabs.postflow.identity.domain.usecase;


import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.event.PasswordResetRequestedEvent;
import io.ketherlabs.postflow.identity.domain.port.HmacTokenGeneratorPort;
import io.ketherlabs.postflow.identity.domain.port.PasswordResetTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.ForgotPasswordCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.ForgotPasswordResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use case UC-AUTH-006 : Demande de réinitialisatiopublic PasswordResetRequestedEvent(UUID id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.occurredAt = Instant.now();
    }n de mot de passe.
 *
 * <p>Flux principal :
 * <ol>
 *   <li>Chercher l'utilisateur par email</li>
 *   <li>Si trouvé ET {@code ACTIVE} : générer token HMAC, persister,
 *       publier {@code PasswordResetRequestedEvent}</li>
 *   <li>Dans tous les cas : retourner réponse générique (anti-énumération)</li>
 * </ol>
 *
 * <p>Le listener sur {@code PasswordResetRequestedEvent} enverra
 * l'email de manière asynchrone via {@code SmtpEmailAdapter}.
 */
public class ForgotPasswordUseCase {


    private final UserRepositoryPort userRepositoryPort;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;
    private final HmacTokenGeneratorPort hmacTokenGeneratorPort;
    private final ApplicationEventPublisher eventPublisher;


    public ForgotPasswordUseCase(UserRepositoryPort userRepositoryPort, PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort, HmacTokenGeneratorPort hmacTokenGeneratorPort, ApplicationEventPublisher eventPublisher) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordResetTokenRepositoryPort = passwordResetTokenRepositoryPort;
        this.hmacTokenGeneratorPort = hmacTokenGeneratorPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ForgotPasswordResponse execute(ForgotPasswordCommand command) {

        Optional<User> optionalUser = userRepositoryPort.findByEmail(command.email());

        // Anti-énumération : email inconnu réponse générique sans rien faire
        if (optionalUser.isEmpty()) {
            return ForgotPasswordResponse.generic();
        }

        User user = optionalUser.get();

        // Anti-énumération : compte non actif → réponse générique sans rien faire
        if (user.getStatus() != UserStatus.ACTIVE) {
            return ForgotPasswordResponse.generic();
        }

        // Générer le token HMAC sécurisé
        String rawToken = hmacTokenGeneratorPort.generate();

        // Créer et persister le PasswordResetToken (TTL 1h)
        PasswordResetToken resetToken = PasswordResetToken.create(
                rawToken,
                user.getId()
        );
        passwordResetTokenRepositoryPort.save(resetToken);

        // Publier l'event — le listener enverra l'email @Async
        eventPublisher.publishEvent(new PasswordResetRequestedEvent(
                user.getId(),
                user.getEmail().getValue(),
                rawToken
        ));

        return ForgotPasswordResponse.generic();
    }
}
