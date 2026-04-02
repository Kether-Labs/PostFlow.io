package io.ketherlabs.postflow.identity.domain.entity.valueobject;


import java.util.Objects;

/**
 * Value object représentant le mot de passe hashé d'un utilisateur.
 *
 * <p>Cette classe n'accepte jamais un mot de passe en clair. Elle encapsule
 * exclusivement un hash bcrypt produit en amont par un {@code PasswordEncoder}.
 * Le mot de passe en clair est reçu par le Controller via le DTO, hashé dans
 * le {@code RegisterUseCase}, puis transmis ici via {@link #fromHash(String)}.
 *
 * <p>Le {@code toString()} retourne {@code [PROTECTED]} pour éviter toute
 * exposition accidentelle du hash dans les logs.
 *
 * <p>Aucune dépendance Spring ou JPA. Classe immuable.
 */

public class Password {

    private final String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    /**
     * Construit un {@code Password} à partir d'un hash bcrypt existant.
     *
     * <p>Deux cas d'utilisation :
     * <ul>
     *   <li>Inscription : {@code Password.fromHash(encoder.encode(rawPassword))}</li>
     *   <li>Chargement depuis la DB : {@code Password.fromHash(entity.getPasswordHash())}</li>
     * </ul>
     *
     * @param bcryptHash le hash bcrypt produit par le {@code PasswordEncoder}
     * @throws IllegalArgumentException si {@code bcryptHash} est null ou vide
     */
    public static Password fromHash(String bcryptHash) {
        if (bcryptHash == null || bcryptHash.isBlank())
            throw new IllegalArgumentException("Password hash cannot be blank");
        return new Password(bcryptHash);
    }

    public String getHashedValue() { return hashedValue; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Password other)) return false;
        return hashedValue.equals(other.hashedValue);
    }

    @Override
    public int hashCode() { return Objects.hash(hashedValue); }

    /**
     * Retourne {@code [PROTECTED]} afin d'éviter toute exposition
     * accidentelle du hash dans les logs ou les messages d'erreur.
     */
    @Override
    public String toString() { return "[PROTECTED]"; }
}
