package io.ketherlabs.postflow.identity.domain.port;


/**
 * Port sortant définissant le contrat d'envoi d'emails transactionnels.
 *
 * <p>Utilisé par les use cases Identity pour l'envoi des emails
 * de vérification (UC-AUTH-001) et de réinitialisation de mot de passe
 * (UC-AUTH-006). Les appels sont effectués de manière asynchrone
 * via {@code @Async("notificationExecutor")} dans les listeners.
 *
 * <p>Interface pure — aucune annotation, aucun import Spring ou JavaMail.
 * Implémentée par {@code SmtpEmailAdapter} dans la couche infrastructure.
 */
public interface EmailSenderPort {

    /**
     * Envoi un email de verification
     */
    void sendVerificationEmail(String toEmail, String verificationToken);

    /**
     * Envoi un email de reinitialisation
     */
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
