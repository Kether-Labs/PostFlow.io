package io.ketherlabs.postflow.identity.domain.usecase.input;

public record RegisterCommand(
        String firstname,
        String lastname,
        String email,
        String password
) {

    public RegisterCommand {
        if (firstname == null || firstname.isBlank()) throw new IllegalArgumentException("Firstname is required");

        if (firstname.length() < 3) throw new IllegalArgumentException("Firstname must be at least 3 characters long");

        if (lastname == null || lastname.isBlank()) throw new IllegalArgumentException("Lastname is required");

        if (lastname.length() < 3) throw new IllegalArgumentException("Lastname must be at least 3 characters long");

        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Invalid email format: " + email);

        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");

        if (password.length() < 8) throw new IllegalArgumentException("Password must be at least 6 characters long");
    }
}
