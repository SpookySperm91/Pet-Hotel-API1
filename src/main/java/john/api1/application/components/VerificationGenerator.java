package john.api1.application.components;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class VerificationGenerator {
    private static final char[] CHAR_ARRAY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;
    private static final int CODE_LENGTH = 6;

    // Base64 URL link
    public static String generateVerificationLink() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        RANDOM.nextBytes(randomBytes); // Use existing SecureRandom instance
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public static String generateToken() {
        byte[] randomBytes = new byte[TOKEN_LENGTH];
        RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    // 6 Random characters code
    public static String generateCode() {
        char[] code = new char[CODE_LENGTH];
        for (int i = 0; i < CODE_LENGTH; i++) {
            code[i] = CHAR_ARRAY[RANDOM.nextInt(CHAR_ARRAY.length)];
        }
        return new String(code);
    }
}
