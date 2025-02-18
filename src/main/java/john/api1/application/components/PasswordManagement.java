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
        return password != null && password.length() >= 8;
    }

}
