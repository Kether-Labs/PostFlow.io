package io.ketherlabs.postflow.identity.domain.exception;

public class AccountNotActiveException extends RuntimeException {
    public AccountNotActiveException(String email) {
        super("The account with email " + email + " is not active. Please activate your account and try again.");
    }
}
