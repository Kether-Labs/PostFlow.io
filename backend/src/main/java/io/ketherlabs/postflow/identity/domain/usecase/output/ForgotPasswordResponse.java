package io.ketherlabs.postflow.identity.domain.usecase.output;

/**
 * Résultat retourné par {@code ForgotPasswordUseCase}.
 *
 * <p>Message générique identique que l'email existe ou non
 * — conformément à la spec anti-énumération UC-AUTH-006.
 *
 * @param message message générique ne révélant pas si l'email est connu
 */
public record ForgotPasswordResponse(String message) {

    public static ForgotPasswordResponse generic() {
        return new ForgotPasswordResponse(
                "If an account exists for this email, " +
                        "you will receive a reset link shortly."
        );
    }
}