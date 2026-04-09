package io.ketherlabs.postflow.identity.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Base64;


@Slf4j
@Configuration
@Getter
public class EmailLogoConfig {

    private String logoBase64;
    /**
     * Charge le logo depuis les ressources statiques et l'encode en Base64
     * pour l'embarquer directement dans le HTML des emails.
     *
     * <p>Appelée une seule fois au démarrage via {@code @PostConstruct}.
     * Stockée en cache dans {@code logoBase64} pour éviter de relire
     * le fichier à chaque envoi.
     */
    @PostConstruct
    private void loadLogo() {
        try {
            ClassPathResource resource = new ClassPathResource("static/logo.png");
            byte[] imageBytes = resource.getInputStream().readAllBytes();
            this.logoBase64 = Base64.getEncoder().encodeToString(imageBytes);
            log.info("Logo loaded successfully for email templates");
        } catch (IOException e) {
            log.warn("Logo not found in static resources — emails will be sent without logo");
            this.logoBase64 = null;
        }
    }
}
