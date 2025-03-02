package john.api1.application.async;

import john.api1.application.components.enums.EmailType;
import john.api1.application.dto.mapper.EmailResponseDTO;
import john.api1.application.ports.services.ISendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AsyncEmailService {
    private final ISendEmail registeredEmail;

    @Autowired
    public AsyncEmailService(@Qualifier("RegisteredEmail") ISendEmail registeredEmail) {
        this.registeredEmail = registeredEmail;

    }

    // @Async("emailTaskExecutor")
    public Mono<EmailResponseDTO> sendEmailAsync(EmailType emailType, String username, String email, String body) {
        return switch (emailType) {
            case REGISTERED -> registeredEmail.sendEmail(username, email, body)
                    .doOnNext(System.out::println); // Optional logging
            default -> Mono.error(new IllegalArgumentException("Unsupported email type: " + emailType));
        };
    }
}
