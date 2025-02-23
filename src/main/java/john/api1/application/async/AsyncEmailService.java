package john.api1.application.async;

import john.api1.application.adapters.services.email.RegisteredEmail;
import john.api1.application.components.enums.EmailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncEmailService {
    private final RegisteredEmail registeredEmail;

    @Autowired
    public AsyncEmailService(RegisteredEmail registeredEmail) {
        this.registeredEmail = registeredEmail;
    }

    @Async("emailTaskExecutor")
    public void sendEmailAsync(EmailType emailType, String username, String email, String body) {
        switch (emailType) {
            case REGISTERED -> registeredEmail.sendEmail(username, email, body);
            default -> throw new IllegalArgumentException("Unsupported email type: " + emailType);
        }
    }
}
