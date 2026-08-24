package io.ketherlabs.postflow.core.infrastructure.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Spring MVC enregistrant les intercepteurs applicatifs.
 *
 * <p>Séparée de {@code SecurityConfig} car les intercepteurs Spring MVC
 * s'exécutent après la security filter chain — ils opèrent au niveau
 * du DispatcherServlet, pas du conteneur servlet.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;

    /**
     * Enregistre {@link RateLimitInterceptor} sur toutes les routes ({@code /**}).
     *
     * <p>Le filtrage auth et global est géré dans l'intercepteur lui-même
     * en inspectant l'URI — pas besoin de deux enregistrements distincts.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor);
    }
}
