package john.api1.application.domain.models;

import john.api1.application.components.PasswordManagement;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class AdminDomain {
    private final String id;
    private String username;
    private String email;
    private String password;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean active;

    public static AdminDomain create(String username, String email) {
        if (!AdminDomain.isValidEmail(email))
            throw new DomainArgumentException("Invalid email format");

        return new AdminDomain(null, username, email, null, Instant.now(), Instant.now(), true);
    }

    public void setPassword(String password, PasswordManagement passwordManagement) {
        if (!passwordManagement.isValid(password))
            throw new DomainArgumentException("Password is too weak. It must be at least 8 characters long and include letters.");

        this.password = passwordManagement.hash(password);
    }

    public boolean validatePassword(String providedPassword, PasswordManagement passwordManagement) {
        if (!active || providedPassword == null || providedPassword.isEmpty())
            return false;

        return passwordManagement.validation(providedPassword, this.password);
    }

    public AdminDomain changeUsername(String username) {
        return new AdminDomain(id, username, email, password, createdAt, Instant.now(), active);
    }

    public AdminDomain changeEmail(String newEmail) {
        if (!isValidEmail(newEmail) || newEmail.equalsIgnoreCase(this.email))
            return this;

        return new AdminDomain(id, username, newEmail, password, createdAt, Instant.now(), active);
    }

    public AdminDomain changePassword(String newPassword, PasswordManagement passwordManagement) {
        if (!passwordManagement.isValid(newPassword))
            throw new DomainArgumentException("Password is too weak. It must be at least 8 characters long and include letters.");

        String newHashedPassword = passwordManagement.hash(newPassword);
        return new AdminDomain(id, username, email, newHashedPassword, createdAt, Instant.now(), active);
    }


    public void setAsInactive() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public void setAsActive() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
