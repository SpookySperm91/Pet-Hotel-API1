package john.api1.application.components;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

// Bcrypt library since Feb 14, 2025
@Component
public class PasswordManagement {

    public String hash(String rawPassword) {
        return  BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public boolean validation(String providedPassword, String hashedPassword) {
        return BCrypt.checkpw(providedPassword, hashedPassword);
    }

    public boolean isValid(String password) {
        if (password == null) {
            System.out.println("Password is null");
            return false;
        }

        password = password.trim(); // Remove accidental spaces

        boolean lengthCheck = password.length() >= 8;
        boolean containsNumber = password.matches(".*\\d.*"); // Checks if it has a number
        boolean containsUppercase = password.matches(".*[A-Z].*"); // Checks if it has a capital letter

        System.out.println("Checking password: [" + password + "]");
        System.out.println("Length check: " + lengthCheck);
        System.out.println("Contains number: " + containsNumber);
        System.out.println("Contains uppercase letter: " + containsUppercase);

        return lengthCheck && (containsNumber || containsUppercase);
    }

}
