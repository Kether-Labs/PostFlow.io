package io.ketherlabs.postflow.identity.application.handler;

import io.ketherlabs.postflow.identity.domain.exception.AccountNotVerifiedException;
import io.ketherlabs.postflow.identity.domain.exception.AccountSuspendedException;
import io.ketherlabs.postflow.identity.domain.exception.EmailAlreadyExistsException;
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

}
