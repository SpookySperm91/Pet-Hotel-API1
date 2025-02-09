package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityType {
    ANIMAL("Animal"),
    BOARDING("Boarding"),
    MEDICAL("Medical"),
    CLIENT("Client");

    private final String typeName;
}

