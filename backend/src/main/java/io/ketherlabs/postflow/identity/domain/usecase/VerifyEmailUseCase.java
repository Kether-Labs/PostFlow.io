package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.VerificationToken;
import io.ketherlabs.postflow.identity.domain.entity.enums.UserStatus;
import io.ketherlabs.postflow.identity.domain.event.UserActivatedEvent;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.exception.TokenAlreadyUsedException;
import io.ketherlabs.postflow.identity.domain.exception.TokenExpiredException;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.VerificationTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.VerifyEmailCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.VerifyEmailResponse;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;


/**
 * Use case UC-AUTH-002 : Vérification de l'adresse email.
 *
 * <p>Flux principal :
 * <ol>
 *   <li>Charger le token depuis la base — sinon {@link InvalidTokenException}</li>
 *   <li>Vérifier TTL 24h — sinon {@link TokenExpiredException}</li>
 *   <li>Vérifier usage unique — sinon {@link TokenAlreadyUsedException}</li>
 *   <li>Activer le compte : {@code status = ACTIVE, emailVerified = true}</li>
 *   <li>Marquer le token comme utilisé</li>
 *   <li>Persister User + Token</li>
 * </ol>
 *
 * <p>Flux alternatif : si le compte est déjà {@code ACTIVE}
 * (token soumis deux fois) → retourner succès silencieusement.
 */
public class VerifyEmailUseCase {

    private final VerificationTokenRepositoryPort verificationTokenRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;


    public VerifyEmailUseCase(VerificationTokenRepositoryPort verificationTokenRepositoryPort, UserRepositoryPort userRepositoryPort, ApplicationEventPublisher eventPublisher) {
        this.verificationTokenRepositoryPort = verificationTokenRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public VerifyEmailResponse execute(VerifyEmailCommand command) {

        // charger le token depuis la base
        VerificationToken verificationToken = verificationTokenRepositoryPort.findByToken(command.token()).
                orElseThrow(InvalidTokenException::new);

        User user = userRepositoryPort.findUserById(verificationToken.getUserid());

        if (user.getStatus() == UserStatus.ACTIVE) {
            return new VerifyEmailResponse(
                    true,
                    "this email is already verified"
            );
        }

        if (verificationToken.isExpired()) {
            throw new TokenExpiredException();
        }

        // vérifier si le token est unique
        if (verificationToken.isUsed()) {
            throw new TokenAlreadyUsedException();
        }

        // marque l'utilisateur comme vérifier
        user.activate();

        // enregistrer le token métier
        verificationTokenRepositoryPort.save(verificationToken);


        // publication l'évenement
        eventPublisher.publishEvent(new UserActivatedEvent(user.getId(), user.getEmail().getValue()));
        return new VerifyEmailResponse(true, "this email is successfully verified");
    }

}
