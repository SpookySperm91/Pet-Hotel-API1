package john.api1.application.components.enums.boarding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestType {
    PHOTO_REQUEST("PHOTO_REQUEST", "Photo Request"),
    VIDEO_REQUEST("VIDEO_REQUEST", "Video Request"),
    GROOMING_SERVICE("GROOMING_SERVICE", "Grooming Service"),
    BOARDING_EXTENSION("BOARDING_EXTENSION", "Boarding Extension"),
    CUSTOM_REQUEST("CUSTOM_REQUEST", "Custom Request");

    private final String requestType;
    private final String requestTypeToDto;
    // private final String requestTypeToMediaDto;  Possible future implementation(?)

    public static RequestType fromString(String requestType) {
        if (requestType == null) {
            return null;
        }

        for (RequestType type : RequestType.values()) {
            if (type.getRequestType().equalsIgnoreCase(requestType)) {
                return type;
            }
        }

        throw new IllegalArgumentException("Unknown request type: " + requestType);
    }

}
