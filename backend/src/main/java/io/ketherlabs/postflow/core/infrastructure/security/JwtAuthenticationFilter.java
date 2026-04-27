package io.ketherlabs.postflow.core.infrastructure.security;

import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtre Spring Security chargé de valider le JWT Bearer à chaque requête.
 *
 * <p>Intercale avant {@code UsernamePasswordAuthenticationFilter} dans la
 * security chain (voir {@link SecurityConfig}). S'exécute exactement une
 * fois par requête grâce à {@link OncePerRequestFilter}.
 *
 * <p>Flux de traitement :
 * <ol>
 *   <li>Extraction du header {@code Authorization: Bearer <token>}.
 *       Si absent ou mal formé, la requête est transmise sans authentification
 *       (Spring Security gérera l'accès selon les règles d'autorisation).</li>
 *   <li>Validation de la signature RS256 et de l'expiration via
 *       {@link JwtTokenPort#isValid(String)} — retourne {@code 401} si invalide.</li>
 *   <li>Vérification de la blacklist Redis via
 *       {@link RedisBlacklistPort#isBlacklisted(String)} — retourne {@code 401}
 *       si le token a été révoqué (logout).</li>
 *   <li>Population du {@code SecurityContextHolder} avec l'identité de
 *       l'utilisateur ({@code userId} extrait du claim {@code sub}).</li>
 * </ol>
 *
 * <p>Dépend uniquement de ports du domaine {@code identity} — aucun import
 * de technologie concrète (JJWT, Redis) dans ce filtre.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenPort jwtTokenPort;
    private final RedisBlacklistPort redisBlacklistPort;

    /**
     * Point d'entrée du filtre, appelé une fois par requête HTTP.
     *
     * <p>Si le token est absent, le filtre délègue sans interrompre la chaîne :
     * c'est Spring Security qui décidera si la route est publique ou non.
     * Si le token est présent mais invalide ou révoqué, la requête est
     * court-circuitée avec un {@code 401}.
     *
     * @param request     la requête HTTP entrante
     * @param response    la réponse HTTP à compléter en cas de rejet
     * @param filterChain la chaîne de filtres à poursuivre si le token est valide
     * @throws ServletException en cas d'erreur de traitement servlet
     * @throws IOException      en cas d'erreur d'écriture sur la réponse
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        if (!jwtTokenPort.isValid(token)) {
            sendUnauthorized(response, "Invalid or expired token");
            return;
        }

        String jti = jwtTokenPort.extractJti(token);
        if (redisBlacklistPort.isBlacklisted(jti)) {
            sendUnauthorized(response, "Token has been revoked");
            return;
        }

        String userId = jwtTokenPort.extractUserId(token).toString();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId, null, Collections.emptyList()  // Pas de roles pour l'instant
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    /**
     * Écrit une réponse {@code 401 Unauthorized} au format JSON et interrompt
     * la chaîne de filtres.
     *
     * @param response la réponse HTTP à compléter
     * @param message  le message d'erreur à sérialiser dans le corps JSON
     * @throws IOException en cas d'erreur d'écriture sur le flux de sortie
     */
    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
