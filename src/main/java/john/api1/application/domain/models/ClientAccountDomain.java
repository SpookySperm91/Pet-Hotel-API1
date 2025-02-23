package john.api1.application.domain.models;

import john.api1.application.components.PasswordManagement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

// DOMAIN
@Getter
@AllArgsConstructor
public class ClientAccountDomain {
    private final String id;
    private final String email;
    private final String phoneNumber;

    // password hashing use Bcrypt library (Feb 14, 2025)
    private final String hashedPassword;

    @Setter
    private boolean locked;
    private final Instant createdAt;
    private final Instant updatedAt;

    public ClientAccountDomain(String email, String phoneNumber, String hashedPassword) {
        if (!isValidEmail(email)) throw new IllegalArgumentException("Invalid email format");
        if (!isValidPhoneNumber(phoneNumber))
            throw new IllegalArgumentException("Invalid phone-number format or length");

        this.id = null;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.locked = false;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public ClientAccountDomain(String id, String email, String phoneNumber, String hashedPassword, boolean locked) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
        this.locked = locked;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    // password relate
    public boolean validatePassword(String providedPassword, PasswordManagement passwordManagement) {
        if (providedPassword == null || providedPassword.isEmpty()) {
            return false;
        }
        return passwordManagement.validation(providedPassword, this.hashedPassword);
    }

    // change password
    public ClientAccountDomain changePassword(String newPassword, PasswordManagement passwordManagement) {
        if (!passwordManagement.isValid(newPassword)) {
            return this;
        }
        String newHashedPassword = passwordManagement.hash(newPassword);
        return new ClientAccountDomain(id, email, phoneNumber, newHashedPassword, locked, createdAt, Instant.now());
    }

    // change email
    public ClientAccountDomain changeEmail(String newEmail) {
        if (isValidEmail(newEmail) || newEmail.equalsIgnoreCase(this.email)) {
            return this; // Return same object if no change
        }
        return new ClientAccountDomain(id, newEmail, phoneNumber, hashedPassword, locked, createdAt, Instant.now());
    }

    // change phone
    public ClientAccountDomain changePhoneNumber(String newPhoneNumber) {
        if (isValidPhoneNumber(newPhoneNumber) || newPhoneNumber.equals(this.phoneNumber)) {
            return this; // Return same object if no change
        }
        return new ClientAccountDomain(id, email, newPhoneNumber, hashedPassword, locked, createdAt, Instant.now());
    }

    // private methods
    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+?\\d{10,}$");
    }
}
