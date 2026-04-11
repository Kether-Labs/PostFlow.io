package io.ketherlabs.postflow.identity.infrastructure.adpater;


import io.ketherlabs.postflow.identity.domain.port.EmailSenderPort;
import io.ketherlabs.postflow.identity.infrastructure.config.EmailLogoConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * Adaptateur sortant implémentant {@link EmailSenderPort} via JavaMailSender.
 *
 * <p>Envoie des emails HTML transactionnels via SMTP. Les appels sont
 * annotés {@code @Async("notificationExecutor")} — l'envoi ne bloque
 * jamais le thread principal. Une exception d'envoi est loggée en ERROR
 * mais ne remonte jamais au UseCase (comportement défini dans la spec :
 * "logguer en ERROR, ne pas bloquer l'inscription").
 *
 * <p>L'adresse expéditeur et l'URL de base sont configurables
 * via {@code application.yml}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter implements  EmailSenderPort {

    private final JavaMailSender mailSender;

    private final EmailLogoConfig emailLogoConfig;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * {@inheritDoc}
     *
     * <p>Envoie un email HTML avec le lien de vérification.
     * Exécuté de manière asynchrone dans {@code notificationExecutor}.
     * En cas d'échec SMTP : log ERROR, aucune exception propagée.
     *
     * @param toEmail  l'adresse email du destinataire
     * @param verificationToken le token de vérification
     */
    @Async("notificationExecutor")
    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {

        String verificationLink = baseUrl + "/verify-email?token=" + verificationToken;
        String subject = "Vérifiez votre adresse email — PostFlow";
        String htmlContent = buildVerificationEmailHtml(verificationLink);

        sendEmail(toEmail, subject, htmlContent);

    }

    /**
     * {@inheritDoc}
     *
     * <p>Envoie un email HTML avec le lien de réinitialisation.
     * Exécuté de manière asynchrone dans {@code notificationExecutor}.
     * En cas d'échec SMTP : log ERROR, aucune exception propagée.
     *
     * @param toEmail    l'adresse email du destinataire
     * @param resetToken le token HMAC de réinitialisation
     */
    @Async("notificationExecutor")
    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {

        String resetLink = baseUrl + "/reset-password?token=" + resetToken;
        String subject = "Réinitialisez votre mot de passe — PostFlow";
        String htmlContent = buildPasswordResetEmailHtml(resetLink);

        sendEmail(toEmail, subject, htmlContent);

    }

    // private methods //

    private void sendEmail(String toEmail, String subject, String htmlContent) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Email sent successfully to={}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send email to={} subject={} error={}",
                    toEmail, subject, e.getMessage());

        }
    }

    /**
     * Construit le contenu HTML de l'email de vérification avec le logo
     * PostFlow embarqué en Base64 au centre de l'email.
     *
     * @param verificationLink le lien complet de vérification
     * @return le contenu HTML de l'email
     */
    private String buildVerificationEmailHtml(String verificationLink) {
        String logoBase64 = emailLogoConfig.getLogoBase64();
        String logoTag    = logoBase64 != null
                ? "<img src=\"data:image/png;base64,%s\" alt=\"PostFlow\" style=\"max-width:200px; height:auto;\"/>".formatted(logoBase64)
                : "<h2 style='color:#ffffff;'>PostFlow</h2>";

        return """
        <!DOCTYPE html>
        <html lang="fr">
          <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          </head>
          <body style="margin:0; padding:0; background-color:#f4f4f5;
                       font-family: Arial, sans-serif;">
            <table width="100%%" cellpadding="0" cellspacing="0"
                   style="background-color:#f4f4f5; padding: 40px 0;">
              <tr>
                <td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background-color:#ffffff; border-radius:12px;
                                overflow:hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">

                    <!-- Header avec logo -->
                    <tr>
                      <td align="center"
                          style="background-color:#4F46E5; padding: 32px 40px;">
                        %s
                      </td>
                    </tr>

                    <!-- Contenu -->
                    <tr>
                      <td style="padding: 40px;">
                        <h2 style="color:#111827; margin-top:0;">
                          Bienvenue sur PostFlow 👋
                        </h2>
                        <p style="color:#6B7280; line-height:1.6;">
                          Merci de vous être inscrit. Cliquez sur le bouton
                          ci-dessous pour vérifier votre adresse email et
                          activer votre compte.
                        </p>

                        <!-- Bouton -->
                        <table cellpadding="0" cellspacing="0" style="margin: 32px 0;">
                          <tr>
                            <td align="center" style="border-radius:8px;
                                         background-color:#4F46E5;">
                              <a href="%s"
                                 style="display:inline-block; padding: 14px 32px;
                                        color:#ffffff; font-size:16px;
                                        font-weight:bold; text-decoration:none;
                                        border-radius:8px;">
                                Vérifier mon email
                              </a>
                            </td>
                          </tr>
                        </table>

                        <p style="color:#9CA3AF; font-size:13px; line-height:1.5;">
                          Ce lien expire dans <strong>24 heures</strong>.<br/>
                          Si vous n'avez pas créé de compte sur PostFlow,
                          ignorez simplement cet email.
                        </p>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td align="center"
                          style="background-color:#F9FAFB; padding: 20px 40px;
                                 border-top: 1px solid #E5E7EB;">
                        <p style="color:#9CA3AF; font-size:12px; margin:0;">
                          © 2025 PostFlow.io — Tous droits réservés
                        </p>
                      </td>
                    </tr>

                  </table>
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(logoTag, verificationLink);
    }

    /**
     * Construit le contenu HTML de l'email de réinitialisation de mot de passe
     * avec le logo PostFlow embarqué en Base64 au centre de l'email.
     *
     * @param resetLink le lien complet de réinitialisation
     * @return le contenu HTML de l'email
     */
    private String buildPasswordResetEmailHtml(String resetLink) {
        String logoBase64 = emailLogoConfig.getLogoBase64();
        String logoTag    = logoBase64 != null
                ? "<img src=\"data:image/png;base64,%s\" alt=\"PostFlow\" style=\"max-width:200px; height:auto;\"/>".formatted(logoBase64)
                : "<h2 style='color:#ffffff;'>PostFlow</h2>";

        return """
        <!DOCTYPE html>
        <html lang="fr">
          <head>
            <meta charset="UTF-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          </head>
          <body style="margin:0; padding:0; background-color:#f4f4f5;
                       font-family: Arial, sans-serif;">
            <table width="100%%" cellpadding="0" cellspacing="0"
                   style="background-color:#f4f4f5; padding: 40px 0;">
              <tr>
                <td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background-color:#ffffff; border-radius:12px;
                                overflow:hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08);">

                    <!-- Header avec logo -->
                    <tr>
                      <td align="center"
                          style="background-color:#4F46E5; padding: 32px 40px;">
                        %s
                      </td>
                    </tr>

                    <!-- Contenu -->
                    <tr>
                      <td style="padding: 40px;">
                        <h2 style="color:#111827; margin-top:0;">
                          Réinitialisation de mot de passe 🔐
                        </h2>
                        <p style="color:#6B7280; line-height:1.6;">
                          Vous avez demandé la réinitialisation de votre mot de passe.
                          Cliquez sur le bouton ci-dessous pour en choisir un nouveau.
                        </p>

                        <!-- Bouton -->
                        <table cellpadding="0" cellspacing="0" style="margin: 32px 0;">
                          <tr>
                            <td align="center" style="border-radius:8px;
                                         background-color:#4F46E5;">
                              <a href="%s"
                                 style="display:inline-block; padding: 14px 32px;
                                        color:#ffffff; font-size:16px;
                                        font-weight:bold; text-decoration:none;
                                        border-radius:8px;">
                                Réinitialiser mon mot de passe
                              </a>
                            </td>
                          </tr>
                        </table>

                        <!-- Avertissement sécurité -->
                        <table cellpadding="0" cellspacing="0"
                               style="background-color:#FEF3C7; border-radius:8px;
                                      margin-bottom: 24px; width:100%%;">
                          <tr>
                            <td style="padding: 16px;">
                              <p style="color:#92400E; font-size:13px;
                                        margin:0; line-height:1.5;">
                                ⚠️ Si vous n'avez pas demandé cette réinitialisation,
                                ignorez cet email. Votre mot de passe ne sera pas modifié.
                              </p>
                            </td>
                          </tr>
                        </table>

                        <p style="color:#9CA3AF; font-size:13px; line-height:1.5;">
                          Ce lien expire dans <strong>1 heure</strong>.
                        </p>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td align="center"
                          style="background-color:#F9FAFB; padding: 20px 40px;
                                 border-top: 1px solid #E5E7EB;">
                        <p style="color:#9CA3AF; font-size:12px; margin:0;">
                          © 2025 PostFlow.io — Tous droits réservés
                        </p>
                      </td>
                    </tr>

                  </table>
                </td>
              </tr>
            </table>
          </body>
        </html>
        """.formatted(logoTag, resetLink);
    }
}
