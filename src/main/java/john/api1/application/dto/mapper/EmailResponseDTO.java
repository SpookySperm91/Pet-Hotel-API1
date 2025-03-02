package john.api1.application.dto.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmailResponseDTO {
    private String id;
    private int status;
    private String message;
    private boolean success;
}
