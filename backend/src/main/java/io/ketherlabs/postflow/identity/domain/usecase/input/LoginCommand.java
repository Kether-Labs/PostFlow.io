package io.ketherlabs.postflow.identity.domain.usecase.input;

public record LoginCommand(
        String email,
        String password
) {

    public LoginCommand {

        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Invalid email format: " + email);

        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
    }

}
