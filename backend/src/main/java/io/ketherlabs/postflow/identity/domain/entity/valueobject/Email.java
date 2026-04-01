package io.ketherlabs.postflow.identity.domain.entity.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Cette classe est une value Object qui nous permet de
 * check le fomart d'un email grace au Regex avant toute utilisation
 * dans le reste de code
 */
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    /**
     * Method Factory qui nous permet de verifier l'email
     * @param raw email a vérifier
     * @return l'email cheecked
     */
    public static Email of(String raw) {
        if (raw == null || raw.isBlank())
            throw new IllegalArgumentException("Email cannot be blank");
        String normalized = raw.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches())
            throw new IllegalArgumentException("Invalid email format: " + raw);
        return new Email(normalized);
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}
