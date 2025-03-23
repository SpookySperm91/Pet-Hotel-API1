package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestType {
    PHOTO_REQUEST("PHOTO_REQUEST"),
    VIDEO_REQUEST("VIDEO_REQUEST"),
    GROOMING_SERVICE("GROOMING_SERVICE"),
    BOARDING_EXTENSION("BOARDING_EXTENSION"),
    CUSTOM_REQUEST("CUSTOM_REQUEST");

    private final String requestType;
}
