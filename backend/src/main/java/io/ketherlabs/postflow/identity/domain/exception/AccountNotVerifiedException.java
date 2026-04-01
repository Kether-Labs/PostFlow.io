package io.ketherlabs.postflow.identity.domain.exception;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException(String email) {
        super("Account not verified email for " +email);
    }
}
