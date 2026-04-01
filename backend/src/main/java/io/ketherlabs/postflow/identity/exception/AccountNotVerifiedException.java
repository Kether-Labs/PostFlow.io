package io.ketherlabs.postflow.identity.exception;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException(String email) {
        super("Account not verified email for " +email);
    }
}
