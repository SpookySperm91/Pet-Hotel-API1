package john.api1.application.adapters.services.email;

import john.api1.application.dto.mapper.EmailResponseDTO;
import john.api1.application.ports.services.ISendEmail;
import john.api1.common.config.MailGunConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public abstract class EmailBaseSend implements ISendEmail {
    private final MailGunConfig mail;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(EmailBaseSend.class);

    @Autowired
    public EmailBaseSend(MailGunConfig mail, @Qualifier("mailgunWebClient") WebClient webClient) {
        this.mail = mail;
        this.webClient = webClient;
    }

    protected abstract String loadEmailTemplate();

    protected abstract String setEmailBody(String emailTemplate, String username, String body);


    @Override
    public Mono<EmailResponseDTO> sendEmail(String username, String email, String body) {
        String apiKey = mail.getApiKey();
        String domain = "bigpawspethotel.tech";

        if (apiKey == null || apiKey.isBlank()) {
            logger.error("Mailgun API key is missing!");
            return Mono.empty();
        }
        String emailTemplate = loadEmailTemplate();
        String emailBody = setEmailBody(emailTemplate, username, body);

        return webClient.post()
                .uri("/{domain}/messages", domain)
                .headers(headers -> headers.setBasicAuth("api", apiKey))
                .bodyValue(buildEmailRequest(email, username, emailBody))
                .retrieve()
                .bodyToMono(MailgunResponse.class)
                .map(response -> {
                    logger.info("Email sent successfully to {}", email);
                    return new EmailResponseDTO(
                            response.id(),
                            200,
                            response.message(),
                            true
                    );
                })
                .onErrorResume(e -> {
                    logger.error("Error sending email to {}: {}", email, e.getMessage());
                    return Mono.just(new EmailResponseDTO(null, 500, "Error: " + e.getMessage(), false));
                });
    }

    private MultiValueMap<String, String> buildEmailRequest(String email, String username, String emailBody) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("from", "BigPaws <bigpaws@bigpawspethotel.tech>");
        formData.add("to", email);
        formData.add("subject", "Hello " + username);
        formData.add("html", emailBody);
        return formData;
    }

    private record MailgunResponse(String id, String message) {
    }
}
