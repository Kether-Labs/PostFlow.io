package io.ketherlabs.postflow.identity.infrastructure.config;

import io.ketherlabs.postflow.identity.domain.port.HmacTokenGeneratorPort;
import io.ketherlabs.postflow.identity.domain.port.JwtTokenPort;
import io.ketherlabs.postflow.identity.domain.port.PasswordEncoderPort;
import io.ketherlabs.postflow.identity.domain.port.PasswordResetTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.RedisBlacklistPort;
import io.ketherlabs.postflow.identity.domain.port.RefreshTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.UserRepositoryPort;
import io.ketherlabs.postflow.identity.domain.port.VerificationTokenRepositoryPort;
import io.ketherlabs.postflow.identity.domain.usecase.ForgotPasswordUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.LoginUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.LogoutUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.RefreshTokenUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.RegisterUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.ResetPasswordUseCase;
import io.ketherlabs.postflow.identity.domain.usecase.VerifyEmailUseCase;
import io.ketherlabs.postflow.identity.infrastructure.adpater.HmacTokenGeneratorAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class IdentityUseCaseConfig {

    @Bean
    PasswordEncoderPort passwordEncoderPort() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return new PasswordEncoderPort() {
            @Override
            public String encode(String rawPassword) {
                return encoder.encode(rawPassword);
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return encoder.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Bean
    HmacTokenGeneratorPort hmacTokenGeneratorPort() {
        return new HmacTokenGeneratorAdapter();
    }

    @Bean
    RegisterUseCase registerUseCase(
            UserRepositoryPort users,
            VerificationTokenRepositoryPort verificationTokens,
            ApplicationEventPublisher events) {
        return new RegisterUseCase(users, verificationTokens, events);
    }

    @Bean
    LoginUseCase loginUseCase(
            UserRepositoryPort users,
            PasswordEncoderPort passwordEncoder,
            JwtTokenPort jwtTokens,
            RefreshTokenRepositoryPort refreshTokens) {
        return new LoginUseCase(users, passwordEncoder, jwtTokens, refreshTokens);
    }

    @Bean
    RefreshTokenUseCase refreshTokenUseCase(
            RefreshTokenRepositoryPort refreshTokens,
            UserRepositoryPort users,
            JwtTokenPort jwtTokens) {
        return new RefreshTokenUseCase(refreshTokens, users, jwtTokens);
    }

    @Bean
    LogoutUseCase logoutUseCase(JwtTokenPort jwtTokens, RedisBlacklistPort blacklist) {
        return new LogoutUseCase(jwtTokens, blacklist);
    }

    @Bean
    VerifyEmailUseCase verifyEmailUseCase(
            VerificationTokenRepositoryPort verificationTokens,
            UserRepositoryPort users,
            ApplicationEventPublisher events) {
        return new VerifyEmailUseCase(verificationTokens, users, events);
    }

    @Bean
    ForgotPasswordUseCase forgotPasswordUseCase(
            UserRepositoryPort users,
            PasswordResetTokenRepositoryPort resetTokens,
            HmacTokenGeneratorPort tokenGenerator,
            ApplicationEventPublisher events) {
        return new ForgotPasswordUseCase(users, resetTokens, tokenGenerator, events);
    }

    @Bean
    ResetPasswordUseCase resetPasswordUseCase(
            PasswordResetTokenRepositoryPort resetTokens,
            UserRepositoryPort users,
            RefreshTokenRepositoryPort refreshTokens,
            PasswordEncoderPort passwordEncoder) {
        return new ResetPasswordUseCase(resetTokens, users, refreshTokens, passwordEncoder);
    }
}
