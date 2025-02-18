package john.api1.application.components;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordCreation {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    int STRENGTH = 10;

    public String randomPassword() {
        StringBuilder password = new StringBuilder(STRENGTH);
        for (int i = 0; i < STRENGTH; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }
}