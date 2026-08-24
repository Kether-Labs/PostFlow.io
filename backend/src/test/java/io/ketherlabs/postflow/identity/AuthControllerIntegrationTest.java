package io.ketherlabs.postflow.identity;

import io.ketherlabs.postflow.identity.application.handler.IdentityExceptionHandler;
import io.ketherlabs.postflow.identity.domain.exception.EmailAlreadyExistsException;
import io.ketherlabs.postflow.identity.domain.usecase.*;
import io.ketherlabs.postflow.identity.domain.usecase.input.RefreshTokenCommand;
import io.ketherlabs.postflow.identity.domain.usecase.output.*;
import io.ketherlabs.postflow.identity.presentation.controller.AuthController;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerIntegrationTest {

    private RegisterUseCase registerUseCase;
    private LoginUseCase loginUseCase;
    private RefreshTokenUseCase refreshTokenUseCase;
    private LogoutUseCase logoutUseCase;
    private VerifyEmailUseCase verifyEmailUseCase;
    private ForgotPasswordUseCase forgotPasswordUseCase;
    private ResetPasswordUseCase resetPasswordUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registerUseCase = mock(RegisterUseCase.class);
        loginUseCase = mock(LoginUseCase.class);
        refreshTokenUseCase = mock(RefreshTokenUseCase.class);
        logoutUseCase = mock(LogoutUseCase.class);
        verifyEmailUseCase = mock(VerifyEmailUseCase.class);
        forgotPasswordUseCase = mock(ForgotPasswordUseCase.class);
        resetPasswordUseCase = mock(ResetPasswordUseCase.class);

        AuthController controller = new AuthController(registerUseCase, loginUseCase,
                refreshTokenUseCase, logoutUseCase, verifyEmailUseCase,
                forgotPasswordUseCase, resetPasswordUseCase);
        mockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new IdentityExceptionHandler())
                .build();
    }

    @Test
    void registerReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        when(registerUseCase.execute(any())).thenReturn(
                new RegisterResponse(userId, "jane@example.com", "registered"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstname":"Jane","lastname":"Doe","email":"jane@example.com","password":"password123"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    void loginReturnsTokensAndSecureHttpOnlyCookie() throws Exception {
        when(loginUseCase.execute(any())).thenReturn(new LoginResponse("access.jwt", "refresh-value"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"jane@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access.jwt"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-value"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.containsString("refreshToken=refresh-value"),
                        org.hamcrest.Matchers.containsString("HttpOnly"),
                        org.hamcrest.Matchers.containsString("Secure"),
                        org.hamcrest.Matchers.containsString("SameSite=Strict"))));
    }

    @Test
    void refreshReadsTokenFromCookieBeforeBody() throws Exception {
        when(refreshTokenUseCase.execute(any())).thenReturn(new RefreshTokenResponse("new-access.jwt"));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refreshToken", "cookie-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"body-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access.jwt"));

        ArgumentCaptor<RefreshTokenCommand> captor = ArgumentCaptor.forClass(RefreshTokenCommand.class);
        verify(refreshTokenUseCase).execute(captor.capture());
        assertThat(captor.getValue().token()).isEqualTo("cookie-token");
    }

    @Test
    void refreshFallsBackToRequestBody() throws Exception {
        when(refreshTokenUseCase.execute(any())).thenReturn(new RefreshTokenResponse("new-access.jwt"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"body-token\"}"))
                .andExpect(status().isOk());

        verify(refreshTokenUseCase).execute(new RefreshTokenCommand("body-token"));
    }

    @Test
    void refreshWithoutTokenReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }

    @Test
    void logoutClearsRefreshCookie() throws Exception {
        when(logoutUseCase.execute(any())).thenReturn(new LogoutResponse(true, "Logged out successfully"));

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accessToken\":\"access.jwt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));
    }

    @Test
    void verifyEmailUsesQueryToken() throws Exception {
        when(verifyEmailUseCase.execute(any())).thenReturn(new VerifyEmailResponse(true, "verified"));

        mockMvc.perform(post("/api/auth/verify-email").queryParam("token", "verification-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void forgotPasswordKeepsGenericResponse() throws Exception {
        when(forgotPasswordUseCase.execute(any())).thenReturn(ForgotPasswordResponse.generic());

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"jane@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void resetPasswordReturnsSuccess() throws Exception {
        when(resetPasswordUseCase.execute(any())).thenReturn(ResetPasswordResponse.success());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"reset-token","newPassword":"newPassword123","confirmPassword":"newPassword123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void domainExceptionIsMappedToConflict() throws Exception {
        when(registerUseCase.execute(any())).thenThrow(new EmailAlreadyExistsException("jane@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstname":"Jane","lastname":"Doe","email":"jane@example.com","password":"password123"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("EMAIL_ALREADY_EXISTS"));
    }
}
