package io.ketherlabs.postflow.core.security;

import io.ketherlabs.postflow.core.infrastructure.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationFilterTest {

    private static final String VALID_TOKEN     = "valid.header.signature";
    private static final String INVALID_TOKEN   = "bad.token.here";
    private static final String FIXED_JTI       = "jti-test-abc-123";
    private static final UUID FIXED_USER_ID   = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");


    // ─── Setup ───────────────────────────────────────────────────────────────

    private FakeJwtTokenAdapter fakeJwt;
    private FakeRedisBlacklistAdapter fakeBlacklist;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        fakeJwt = new FakeJwtTokenAdapter();
        fakeBlacklist = new FakeRedisBlacklistAdapter();
        filter = new JwtAuthenticationFilter(fakeJwt, fakeBlacklist);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    // ─── # Requête sans token ────────────────────────────────────────────────

    @Test
    void should_pass_through_when_no_authorization_header() throws Exception {
        MockHttpServletRequest  request  = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain         chain    = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest(), "la chaîne doit avoir été invoquée");
        assertEquals(200, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void should_pass_through_when_basic_auth_header() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain         chain    = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest(), "Basic auth doit passer sans interférence du filtre JWT");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // ─── # Token invalide ────────────────────────────────────────────────────

    @Test
    void should_return_401_when_token_is_invalid() throws Exception {
        fakeJwt.makeInvalid();
        MockHttpServletRequest  request  = buildBearerRequest(INVALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain         chain    = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(chain.getRequest(), "la chaîne NE doit PAS être invoquée après un 401");
    }

    @Test
    void should_return_error_message_when_token_is_invalid() throws Exception {
        fakeJwt.makeInvalid();
        MockHttpServletRequest  request  = buildBearerRequest(INVALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertTrue(response.getContentAsString().contains("Invalid or expired token"));
    }

    @Test
    void should_return_json_content_type_when_token_is_invalid() throws Exception {
        fakeJwt.makeInvalid();
        MockHttpServletRequest  request  = buildBearerRequest(INVALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("application/json", response.getContentType());
    }

    // ─── # Token blacklisté ──────────────────────────────────────────────────

    @Test
    void should_return_401_when_token_is_blacklisted() throws Exception {
        fakeBlacklist.blacklistJti(FIXED_JTI);
        MockHttpServletRequest  request  = buildBearerRequest(VALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain         chain    = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(chain.getRequest());
    }

    @Test
    void should_return_revoked_message_when_token_is_blacklisted() throws Exception {
        fakeBlacklist.blacklistJti(FIXED_JTI);
        MockHttpServletRequest  request  = buildBearerRequest(VALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertTrue(response.getContentAsString().contains("Token has been revoked"));
    }

    // ─── # Token valide ──────────────────────────────────────────────────────

    @Test
    void should_pass_through_when_token_is_valid() throws Exception {
        MockHttpServletRequest  request  = buildBearerRequest(VALID_TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain         chain    = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNotNull(chain.getRequest());
        assertEquals(200, response.getStatus());
    }

    @Test
    void should_set_authenticated_security_context_when_token_is_valid() throws Exception {
        MockHttpServletRequest request = buildBearerRequest(VALID_TOKEN);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
    }

    @Test
    void should_set_user_id_as_principal_when_token_is_valid() throws Exception {
        MockHttpServletRequest request = buildBearerRequest(VALID_TOKEN);

        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(FIXED_USER_ID.toString(), auth.getPrincipal());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private MockHttpServletRequest buildBearerRequest(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
