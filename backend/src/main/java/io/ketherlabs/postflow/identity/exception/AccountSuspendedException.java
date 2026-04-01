package io.ketherlabs.postflow.identity.exception;

public class AccountSuspendedException extends RuntimeException {
    public AccountSuspendedException(String email) {
        super("Account suspended email for " +email);
    }
}
