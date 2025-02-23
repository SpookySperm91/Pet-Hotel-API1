package john.api1.application.adapters.services.email.body;

// Password Reset Email Format
public record PasswordResetEmail(String email, String resetToken) implements EmailDetails {
    @Override
    public String format() {
        return email + "," + resetToken;
    }
}
