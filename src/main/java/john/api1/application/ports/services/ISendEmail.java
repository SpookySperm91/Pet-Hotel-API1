package john.api1.application.ports.services;

import john.api1.application.dto.mapper.EmailResponseDTO;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ISendEmail {
    Mono<EmailResponseDTO> sendEmail(String username, String email, String body);
}

