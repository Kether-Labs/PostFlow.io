package io.ketherlabs.postflow.identity.presentation.controller;


import io.ketherlabs.postflow.identity.domain.usecase.*;
import io.ketherlabs.postflow.identity.domain.usecase.input.*;
import io.ketherlabs.postflow.identity.domain.usecase.output.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Identity authentication endpoints")
public class AuthController {

    static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final long REFRESH_TOKEN_MAX_AGE_SECONDS = 7 * 24 * 60 * 60;

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    public AuthController(
            RegisterUseCase registerUseCase,
            LoginUseCase loginUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            LogoutUseCase logoutUseCase,
            VerifyEmailUseCase verifyEmailUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase,
            ResetPasswordUseCase resetPasswordUseCase
    ) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.verifyEmailUseCase = verifyEmailUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registerUseCase.execute(command));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT + refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginCommand command,
            HttpServletResponse response
    ) {
        LoginResponse result = loginUseCase.execute(command);

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie(
                result.refreshToken(), REFRESH_TOKEN_MAX_AGE_SECONDS).toString());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Generate new access token using refresh token")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String cookieToken,
            @RequestBody(required = false) RefreshTokenCommand command
    ) {
        String token = cookieToken != null && !cookieToken.isBlank()
                ? cookieToken
                : command == null ? null : command.token();

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        return ResponseEntity.ok(refreshTokenUseCase.execute(new RefreshTokenCommand(token)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user (blacklist JWT)")
    public ResponseEntity<LogoutResponse> logout(
            @RequestBody(required = false) LogoutCommand command,
            HttpServletResponse response
    ) {
        LogoutCommand effectiveCommand = command == null ? new LogoutCommand(null) : command;
        LogoutResponse result = logoutUseCase.execute(effectiveCommand);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie("", 0).toString());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify user email with token")
    public ResponseEntity<VerifyEmailResponse> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(
                verifyEmailUseCase.execute(new VerifyEmailCommand(token))
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Send password reset email")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordCommand command
    ) {
        return ResponseEntity.ok(forgotPasswordUseCase.execute(command));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password using token")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Valid @RequestBody ResetPasswordCommand command
    ) {
        return ResponseEntity.ok(resetPasswordUseCase.execute(command));
    }

    private ResponseCookie refreshTokenCookie(String value, long maxAgeSeconds) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
