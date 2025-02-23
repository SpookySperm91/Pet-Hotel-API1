package john.api1.application.adapters.services.email.body;

// Registration Email Format
public record RegistrationEmailData(String email, String phone, String password) implements EmailDetails {
    @Override
    public String format() {
        return email + "|" + phone + "|" + password;
    }
}
