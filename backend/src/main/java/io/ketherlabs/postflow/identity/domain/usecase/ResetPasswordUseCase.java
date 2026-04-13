package io.ketherlabs.postflow.identity.domain.usecase;

import io.ketherlabs.postflow.identity.domain.entity.PasswordResetToken;
import io.ketherlabs.postflow.identity.domain.entity.User;
import io.ketherlabs.postflow.identity.domain.entity.valueobject.Password;
import io.ketherlabs.postflow.identity.domain.exception.InvalidTokenException;
import io.ketherlabs.postflow.identity.domain.exception.TokenAlreadyUsedException;
import io.ketherlabs.postflow.identity.domain.exception.TokenExpiredException;
import io.ketherlabs.postflow.identity.domain.port.PasswordEncoderPort;
import io.ketherlabs.postflow.identity.domain.port.PasswordResetTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.input.ResetPasswordCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.ResetPasswordResponse;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case UC-AUTH-007 : Réinitialisation du mot de passe.
 *
 * <p>Flux principal :
 * <ol>
 *   <li>Charger le token depuis la base — sinon {@link InvalidTokenException}</li>
 *   <li>Vérifier TTL 1h — sinon {@link TokenExpiredException}</li>
 *   <li>Vérifier usage unique — sinon {@link TokenAlreadyUsedException}</li>
 *   <li>Charger l'utilisateur associé</li>
 *   <li>Hasher le nouveau mot de passe via {@code PasswordEncoderPort}</li>
 *   <li>Appeler {@code user.changePassword()} — méthode métier domaine</li>
 *   <li>Persister l'utilisateur avec le nouveau hash</li>
 *   <li>Marquer le token comme utilisé</li>
 *   <li>Révoquer tous les Refresh Tokens actifs — forcer reconnexion</li>
 * </ol>
 */
public class ResetPasswordUseCase {

    private final PasswordResetTokenRepositoryPort tokenRepository;
    private final UserRepositoryPort               userRepository;
    private final RefreshTokenRepositoryPort       refreshTokenRepository;
    private final PasswordEncoderPort              passwordEncoder;

    public ResetPasswordUseCase(
            PasswordResetTokenRepositoryPort tokenRepository,
            UserRepositoryPort userRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            PasswordEncoderPort passwordEncoder) {
        this.tokenRepository        = tokenRepository;
        this.userRepository         = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder        = passwordEncoder;
    }

    /**
     * Exécute la réinitialisation du mot de passe.
     *
     * @param command la commande contenant le token et le nouveau mot de passe
     * @return une réponse de confirmation
     * @throws InvalidTokenException     si le token est introuvable
     * @throws TokenExpiredException     si le token a expiré (TTL 1h)
     * @throws TokenAlreadyUsedException si le token a déjà été consommé
     */
    @Transactional
    public ResetPasswordResponse execute(ResetPasswordCommand command) {

        // 1. Charger le token depuis la base
        PasswordResetToken resetToken = tokenRepository
                .findByTokenHash(command.token())
                .orElseThrow(InvalidTokenException::new);

        // 2. Vérifier TTL 1h
        if (resetToken.isExpired()) {
            throw new TokenExpiredException();
        }

        // 3. Vérifier usage unique
        if (resetToken.isUsed()) {
            throw new TokenAlreadyUsedException();
        }

        // 4. Charger l'utilisateur associé
        User user = userRepository
                .findById(resetToken.getUserId())
                .orElseThrow(InvalidTokenException::new);

        // 5. Hasher le nouveau mot de passe
        String newHash = passwordEncoder.encode(command.newPassword());

        // 6. Changer le mot de passe via la méthode métier
        user.changePassword(Password.fromHash(newHash));

        // 7. Persister l'utilisateur avec le nouveau hash
        userRepository.save(user);

        // 8. Marquer le token comme utilisé
        tokenRepository.markAsUsed(resetToken);

        // 9. Révoquer tous les Refresh Tokens actifs → forcer reconnexion
        refreshTokenRepository.revokeAllByUserId(user.getId());

        return ResetPasswordResponse.success();
    }
}