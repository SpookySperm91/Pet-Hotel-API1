package john.api1.application.adapters.services.email.body;

public sealed interface EmailDetails permits RegistrationEmailData, PasswordResetEmail {
    String format();
}

