package io.ketherlabs.postflow.identity.domain.port;


/**
 * Port sortant définissant le contrat de génération
 * des tokens HMAC-SHA256 pour la réinitialisation de mot de passe.
 *
 * <p>Interface pure — aucune dépendance Spring ou crypto.
 * Implémentée par {@code HmacTokenGeneratorAdapter}
 * dans la couche infrastructure.
 */

public interface HmacTokenGeneratorPort {

    /**
     * Génère un token HMAC-SHA256 sécurisé à usage unique.
     *
     * <p>Utilisé par {@code ForgotPasswordUseCase} (UC-AUTH-006)
     * pour générer le token inclus dans le lien de réinitialisation.
     *
     * @return un token HMAC signé sous forme de String
     */
    String generate();
}
