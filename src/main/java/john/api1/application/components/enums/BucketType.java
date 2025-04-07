package john.api1.application.components.enums;

import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BucketType {
    PROFILE_PHOTO(120, "PROFILE_PHOTO"),
    REQUEST_PHOTO(120, "REQUEST_PHOTO"),
    REQUEST_VIDEO(120, "REQUEST_VIDEO"),
    SERVICE_PHOTO(120, "SERVICE_PHOTO"),
    SERVICE_VIDEO(120, "SERVICE_VIDEO");

    private final int minuteExpire;
    private final String bucketType;

    public static BucketType fromString(String bucketTypeString) {
        for (BucketType type : BucketType.values()) {
            if (type.bucketType.equalsIgnoreCase(bucketTypeString)) {
                return type;
            }
        }
        throw new DomainArgumentException("No BucketType with value: " + bucketTypeString);
    }
}