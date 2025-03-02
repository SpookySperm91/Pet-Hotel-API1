package john.api1.application.dto.mapper;

import john.api1.application.services.response.RegisterResponse;
import org.springframework.http.HttpStatus;

import java.time.Instant;


public record RegisterResponseDTO(
        RegisterResponse data
) {
}
