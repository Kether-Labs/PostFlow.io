package io.ketherlabs.postflow.identity.domain.exception;

public class AccountSuspendedException extends RuntimeException {
    public AccountSuspendedException(String email) {
        super("Account suspended email for " +email);
    }
}
