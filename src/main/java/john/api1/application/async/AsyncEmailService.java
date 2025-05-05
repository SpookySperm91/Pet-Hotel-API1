package john.api1.application.async;

import john.api1.application.components.enums.EmailType;
import john.api1.application.dto.mapper.EmailResponseDTO;
import john.api1.application.ports.services.ISendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AsyncEmailService {
    private final ISendEmail registeredEmail;
    private final ISendEmail passwordResetLink;
    private final ISendEmail adminPasswordResetLink;


    @Autowired
    public AsyncEmailService(@Qualifier("RegisteredEmail") ISendEmail registeredEmail,
                             @Qualifier("PasswordResetLink") ISendEmail passwordResetLink,
                             @Qualifier("PasswordResetLink") ISendEmail adminPasswordResetLink) {
        this.registeredEmail = registeredEmail;
        this.passwordResetLink = passwordResetLink;
        this.adminPasswordResetLink = adminPasswordResetLink;

    }

    public Mono<EmailResponseDTO> sendEmailAsync(EmailType emailType, String username, String email, String body) {
        return switch (emailType) {
            case REGISTERED -> registeredEmail.sendEmail(username, email, body)
                    .doOnNext(System.out::println); // Optional logging
            case RESET_PASSWORD_LINK -> passwordResetLink.sendEmail(username, email, body)
                    .doOnNext(System.out::println);
            case ADMIN_RESET_PASSWORD_LINK -> adminPasswordResetLink.sendEmail(username, email, body)
                    .doOnNext(System.out::println);
            default -> Mono.error(new IllegalArgumentException("Unsupported email type: " + emailType));
        };
    }
}
