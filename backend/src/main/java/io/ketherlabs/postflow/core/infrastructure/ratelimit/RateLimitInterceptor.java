package io.ketherlabs.postflow.core.infrastructure.ratelimit;

import io.ketherlabs.postflow.core.domain.port.RateLimitPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Intercepteur Spring MVC appliquant deux niveaux de rate limiting par IP :
 *
 * <ul>
 *   <li><b>Auth</b> — {@code /api/auth/**} : {@code app.rate-limit.auth.max-requests}
 *       requêtes par minute. Protège le login/register contre le brute-force.</li>
 *   <li><b>Global</b> — toutes les routes : {@code app.rate-limit.global.max-requests}
 *       requêtes par minute. Protection générale contre le scraping et les abus.</li>
 * </ul>
 *
 * <p>Une requête sur {@code /api/auth/**} consomme un slot dans les deux compteurs.
 * Le limite auth (plus restrictive) est vérifiée en premier pour renvoyer
 * le message d'erreur le plus précis.
 *
 * <p>L'IP est extraite du header {@code X-Forwarded-For} si présent (proxy/load
 * balancer), sinon depuis {@code request.getRemoteAddr()}.
 *
 * <p>En cas de dépassement, renvoie {@code 429 Too Many Requests} avec le
 * header {@code Retry-After: 60}.
 */
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String AUTH_PATH_PREFIX = "/api/auth/";
    private static final int    WINDOW_SECONDS    = 60;

    @Value("${app.rate-limit.global.max-requests:100}")
    private int globalMaxRequests;

    @Value("${app.rate-limit.auth.max-requests:10}")
    private int authMaxRequests;

    private final RateLimitPort rateLimitPort;

    /**
     * Vérifie les limites avant que la requête atteigne le contrôleur.
     *
     * @return {@code true} si la requête est autorisée, {@code false} si bloquée (429 envoyé)
     * @throws IOException en cas d'erreur d'écriture sur la réponse
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {

        String ip   = extractClientIp(request);
        String path = request.getRequestURI();

        if (path.startsWith(AUTH_PATH_PREFIX)) {
            if (!rateLimitPort.isAllowed("rate:auth:" + ip, authMaxRequests, WINDOW_SECONDS)) {
                sendTooManyRequests(response, "Too many authentication attempts. Try again in 1 minute.");
                return false;
            }
        }

        if (!rateLimitPort.isAllowed("rate:global:" + ip, globalMaxRequests, WINDOW_SECONDS)) {
            sendTooManyRequests(response, "Too many requests. Try again in 1 minute.");
            return false;
        }

        return true;
    }

    /**
     * Extrait l'IP réelle du client.
     *
     * <p>Consulte d'abord {@code X-Forwarded-For} (premier hop en cas de
     * chaîne de proxies) avant de tomber sur {@code getRemoteAddr()}.
     */
    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Écrit une réponse {@code 429 Too Many Requests} avec le header
     * {@code Retry-After} indiquant la durée d'attente en secondes.
     */
    private void sendTooManyRequests(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", String.valueOf(WINDOW_SECONDS));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
