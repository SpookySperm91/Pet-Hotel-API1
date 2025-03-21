package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketType {
    PROFILE_PHOTO(10),
    REQUEST_PHOTO(120),
    REQUEST_VIDEO(120),
    SERVICE_PHOTO(120),
    SERVICE_VIDEO(120);

    private final int minuteExpire;
}