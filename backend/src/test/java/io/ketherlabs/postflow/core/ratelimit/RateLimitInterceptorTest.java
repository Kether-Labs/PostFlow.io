package io.ketherlabs.postflow.core.ratelimit;

import io.ketherlabs.postflow.core.infrastructure.ratelimit.RateLimitInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitInterceptorTest {

    private static final int GLOBAL_LIMIT = 100;
    private static final int AUTH_LIMIT   = 10;


    // ─── Setup ───────────────────────────────────────────────────────────────

    private FakeRedisRateLimitAdapter fakeRateLimit;
    private RateLimitInterceptor interceptor;

    @BeforeEach
    void setUp() {
        fakeRateLimit = new FakeRedisRateLimitAdapter();
        interceptor   = new RateLimitInterceptor(fakeRateLimit);
        ReflectionTestUtils.setField(interceptor, "globalMaxRequests", GLOBAL_LIMIT);
        ReflectionTestUtils.setField(interceptor, "authMaxRequests",   AUTH_LIMIT);
    }

    // ─── # Limite globale ────────────────────────────────────────────────────

    @Test
    void should_pass_when_first_request_on_any_route() throws Exception {
        MockHttpServletRequest  request  = buildRequest("GET", "/api/posts", "10.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(200, response.getStatus());
    }

    @Test
    void should_pass_when_exactly_at_global_limit() throws Exception {
        MockHttpServletRequest request = buildRequest("GET", "/api/posts", "10.0.0.2");

        for (int i = 0; i < GLOBAL_LIMIT - 1; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object()); // 100ème

        assertTrue(result);
        assertEquals(200, response.getStatus());
    }

    @Test
    void should_return_429_when_global_limit_is_exceeded() throws Exception {
        MockHttpServletRequest request = buildRequest("GET", "/api/posts", "10.0.0.3");

        for (int i = 0; i < GLOBAL_LIMIT; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object()); // 101ème

        assertFalse(result);
        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString().contains("Too many requests"));
    }

    @Test
    void should_include_retry_after_header_when_global_limit_is_exceeded() throws Exception {
        MockHttpServletRequest request = buildRequest("GET", "/api/posts", "10.0.0.4");

        for (int i = 0; i < GLOBAL_LIMIT; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        }
        MockHttpServletResponse response = new MockHttpServletResponse();
        interceptor.preHandle(request, response, new Object());

        assertEquals("60", response.getHeader("Retry-After"));
    }

    @Test
    void should_not_share_counters_between_different_ips() throws Exception {
        MockHttpServletRequest ipA = buildRequest("GET", "/api/posts", "1.1.1.1");
        MockHttpServletRequest ipB = buildRequest("GET", "/api/posts", "2.2.2.2");

        // IP A dépasse la limite
        for (int i = 0; i < GLOBAL_LIMIT; i++) {
            interceptor.preHandle(ipA, new MockHttpServletResponse(), new Object());
        }

        // IP B doit toujours passer
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean result = interceptor.preHandle(ipB, response, new Object());

        assertTrue(result, "IP B should be allowed to make requests even if IP A has exceeded the limit");
    }

    // ─── # Limite auth ───────────────────────────────────────────────────────

    @Test
    void should_pass_when_first_auth_request() throws Exception {
        MockHttpServletRequest  request  = buildRequest("POST", "/api/auth/login", "10.0.0.5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
    }

    @Test
    void should_return_429_when_auth_limit_is_exceeded() throws Exception {
        MockHttpServletRequest request = buildRequest("POST", "/api/auth/login", "10.0.0.6");

        for (int i = 0; i < AUTH_LIMIT; i++) {
            interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        }
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object()); // 11ème

        assertFalse(result);
        assertEquals(429, response.getStatus());
        assertTrue(response.getContentAsString().contains("Too many authentication attempts"));
    }

    @Test
    void should_return_auth_specific_message_when_auth_limit_is_exceeded() throws Exception {
        // Intercepteur avec authMax=2 et globalMax=100 pour isoler le cas auth
        RateLimitInterceptor custom = new RateLimitInterceptor(fakeRateLimit);
        ReflectionTestUtils.setField(custom, "globalMaxRequests", 100);
        ReflectionTestUtils.setField(custom, "authMaxRequests",   2);

        MockHttpServletRequest request = buildRequest("POST", "/api/auth/login", "10.0.0.7");

        custom.preHandle(request, new MockHttpServletResponse(), new Object());
        custom.preHandle(request, new MockHttpServletResponse(), new Object());
        MockHttpServletResponse response = new MockHttpServletResponse();
        custom.preHandle(request, response, new Object()); // 3ème → auth bloqué

        assertTrue(response.getContentAsString().contains("Too many authentication attempts"),
                "The response should contain the auth-specific message when the auth limit is exceeded");
    }

    @Test
    void should_return_global_message_when_auth_ok_but_global_limit_exceeded() throws Exception {
        // authMax=100, globalMax=2 → auth passe, global bloque
        RateLimitInterceptor custom = new RateLimitInterceptor(fakeRateLimit);
        ReflectionTestUtils.setField(custom, "globalMaxRequests", 2);
        ReflectionTestUtils.setField(custom, "authMaxRequests",   100);

        MockHttpServletRequest request = buildRequest("POST", "/api/auth/login", "10.0.0.8");

        custom.preHandle(request, new MockHttpServletResponse(), new Object());
        custom.preHandle(request, new MockHttpServletResponse(), new Object());
        MockHttpServletResponse response = new MockHttpServletResponse();
        custom.preHandle(request, response, new Object()); // 3ème → global bloqué

        assertFalse(custom.preHandle(request, new MockHttpServletResponse(), new Object()));
        assertTrue(response.getContentAsString().contains("Too many requests"));
    }

    @Test
    void should_increment_both_counters_when_auth_route_is_called() throws Exception {
        MockHttpServletRequest request = buildRequest("POST", "/api/auth/register", "10.0.0.9");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals(1, fakeRateLimit.countCallsFor("rate:auth:"),   "compteur auth doit être à 1");
        assertEquals(1, fakeRateLimit.countCallsFor("rate:global:"), "compteur global doit être à 1");
    }

    @Test
    void should_increment_only_global_counter_when_non_auth_route_is_called() throws Exception {
        MockHttpServletRequest request = buildRequest("GET", "/api/posts", "10.0.0.10");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals(0, fakeRateLimit.countCallsFor("rate:auth:"),   "compteur auth NE doit PAS être incrémenté");
        assertEquals(1, fakeRateLimit.countCallsFor("rate:global:"), "compteur global doit être à 1");
    }

    // ─── # Extraction IP ─────────────────────────────────────────────────────

    @Test
    void should_use_first_ip_from_x_forwarded_for_header() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        request.addHeader("X-Forwarded-For", "1.2.3.4, 5.6.7.8, 9.9.9.9");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals(2, fakeRateLimit.countCallsFor("rate:global:1.2.3.4"),
                "l'IP réelle (premier hop) doit être utilisée pour la clé Redis");
    }

    @Test
    void should_use_remote_addr_when_no_proxy_header() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        request.setRemoteAddr("192.168.1.50");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals(1, fakeRateLimit.countCallsFor("rate:global:192.168.1.50"));
    }

    @Test
    void should_fallback_to_remote_addr_when_x_forwarded_for_is_blank() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/posts");
        request.addHeader("X-Forwarded-For", "   ");
        request.setRemoteAddr("172.16.0.1");

        interceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        assertEquals(1, fakeRateLimit.countCallsFor("rate:global:172.16.0.1"));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private MockHttpServletRequest buildRequest(String method, String uri, String remoteAddr) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.setRemoteAddr(remoteAddr);
        return request;
    }
}
