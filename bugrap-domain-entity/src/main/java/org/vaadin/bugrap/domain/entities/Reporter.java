package org.vaadin.bugrap.domain.entities;

import org.vaadin.bugrap.domain.PasswordHash;

import javax.persistence.Entity;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Entity
public class Reporter extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    private String name;

    private String email;

    private String password;

    private boolean admin = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a hashed password. Don't call - use {@link #verifyPassword(String)} instead.
     * @return hashed password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Never call directly - use {@link #hashPassword(String)} instead.
     * @param password
     */
    @Deprecated
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return name;
    }

    public void hashPassword(String password) {
        try {
            setPassword(PasswordHash.createHash(password));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyPassword(String password) {
        try {
            return PasswordHash.validatePassword(password, getPassword());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
