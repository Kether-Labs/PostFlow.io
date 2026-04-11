package io.ketherlabs.postflow.identity.application.handler;

import io.ketherlabs.postflow.identity.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Gestionnaire centralisé des exceptions du module Identity.
 * Convertit les exceptions métier en réponses HTTP standardisées avec codes d'erreur.
 * Applicable à tous les contrôleurs REST du module via {@link RestControllerAdvice}.
 */

@RestControllerAdvice
public class IdentityExceptionHandler {

    public record ErrorResponse(String errorCode, String errorMessage) {}

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotVerifiedException(AccountNotVerifiedException ex) {
        return ResponseEntity.status(403)
                .body(new ErrorResponse("ACCOUNT_NOT_VERIFIED", ex.getMessage()));
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ErrorResponse> handleAccountSuspendedException(AccountSuspendedException ex) {
        return ResponseEntity.status(403)
                .body(new ErrorResponse("ACCOUNT_SUSPENDED", ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(409)
                .body(new ErrorResponse("EMAIL_ALREADY_EXISTS", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(400)
                .body(new ErrorResponse("INVALID_ARGUMENT", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(409)
                .body(new ErrorResponse("INVALID_STATE", ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse("INVALID_TOKEN", ex.getMessage())
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse("TOKEN_EXPIRED", ex.getMessage())
        );
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<ErrorResponse> handleTokenAlreadyUsed(TokenAlreadyUsedException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse("TOKEN_ALREADY_USED", ex.getMessage())
        );
    }

}
