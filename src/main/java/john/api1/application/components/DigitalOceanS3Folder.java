package john.api1.application.components;

import john.api1.application.components.enums.BucketType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DigitalOceanS3Folder {

    private final Map<BucketType, String> bucketMap;

    public DigitalOceanS3Folder(
            @Value("${digital-ocean.bucket.name.profile-photo}") String profilePhotoBucket,
            @Value("${digital-ocean.bucket.name.request-photo}") String requestPhotoBucket,
            @Value("${digital-ocean.bucket.name.request-video}") String requestVideoBucket,
            @Value("${digital-ocean.bucket.name.service-photo}") String servicePhotoBucket,
            @Value("${digital-ocean.bucket.name.service-video}") String serviceVideoBucket) {

        this.bucketMap = Map.of(
                BucketType.PROFILE_PHOTO, profilePhotoBucket,
                BucketType.REQUEST_PHOTO, requestPhotoBucket,
                BucketType.REQUEST_VIDEO, requestVideoBucket,
                BucketType.SERVICE_PHOTO, servicePhotoBucket,
                BucketType.SERVICE_VIDEO, serviceVideoBucket
        );
    }

    public String getBucketName(BucketType type) {
        return bucketMap.getOrDefault(type, null);
    }
}
