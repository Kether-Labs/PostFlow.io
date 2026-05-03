package io.ketherlabs.postflow.identity.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * DTO d'entrée pour POST /api/auth/register.
 * La validation Jakarta est faite ici — la Command domaine
 * reçoit des données déjà validées.
 */
public record RegisterRequest(
        @NotBlank(message = "Firstname is required")
        @Size(min = 3, message = "Firstname must be at least 3 characters")
        String firstname,

        @NotBlank(message = "Lastname is required")
        @Size(min = 3, message = "Lastname must be at least 3 characters")
        String lastname,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password
) {
}
