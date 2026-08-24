package io.ketherlabs.postflow.core.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuration centrale de Spring Security pour l'ensemble de l'application.
 *
 * <p>Positionnée dans {@code core/infrastructure/security} car elle est
 * transversale à tous les domaines (identity, post, billing...) — elle ne
 * dépend d'aucun module métier et protège toutes les routes du projet.
 *
 * <p>Principes appliqués :
 * <ul>
 *   <li><b>Stateless</b> — aucune session HTTP côté serveur ; l'identité
 *       de l'appelant est portée exclusivement par le JWT.</li>
 *   <li><b>CSRF désactivé</b> — inutile en mode stateless (pas de cookies
 *       de session à protéger).</li>
 *   <li><b>CORS whitelist</b> — seule l'origine frontend déclarée dans
 *       {@code app.cors.allowed-origins} peut consommer l'API.</li>
 *   <li><b>Routes publiques</b> — {@code /api/auth/**} et
 *       {@code /actuator/health} sont accessibles sans token.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ROUTES = {
            "/api/auth/**",
            "/actuator/health"
    };

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Déclare la chaîne de filtres Spring Security.
     *
     * <p>Ordre d'application sur chaque requête entrante :
     * <ol>
     *   <li>{@link JwtAuthenticationFilter} — valide le Bearer token et
     *       alimente le {@code SecurityContextHolder}.</li>
     *   <li>Vérification des autorisations — routes publiques libres,
     *       toutes les autres exigent une authentification.</li>
     * </ol>
     *
     * @param http              le configurateur Spring Security fourni par le contexte
     * @param jwtAuthenticationFilter le filtre JWT à intercaler avant l'auth classique
     * @return la {@link SecurityFilterChain} enregistrée dans le contexte Spring
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ROUTES).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Désactive l'auto-enregistrement de {@link JwtAuthenticationFilter}
     * comme filtre servlet global par Spring Boot.
     *
     * <p>Tout bean qui étend {@code OncePerRequestFilter} est automatiquement
     * enregistré par Spring Boot dans la chaîne servlet générale. Sans ce bean,
     * le filtre s'exécuterait deux fois par requête : une fois hors de la
     * security chain (enregistrement auto) et une fois à l'intérieur
     * (via {@code addFilterBefore}).
     *
     * @param filter le filtre JWT géré par Spring
     * @return un {@link FilterRegistrationBean} configuré pour ne pas s'enregistrer
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Configure la politique CORS appliquée à toutes les routes ({@code /**}).
     *
     * <p>Seule l'origine déclarée dans {@code app.cors.allowed-origins}
     * (variable d'environnement {@code FRONTEND_URL}) est autorisée.
     * Plusieurs origines peuvent être séparées par une virgule dans la variable.
     *
     * <p>{@code maxAge: 3600} met en cache le résultat du preflight OPTIONS
     * pendant une heure côté navigateur, évitant une requête OPTIONS
     * supplémentaire à chaque appel API.
     *
     * @return la source de configuration CORS enregistrée dans Spring Security
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
