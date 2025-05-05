package john.api1.application.ports.services.media;

import john.api1.application.components.enums.BucketType;

public interface IMediaAdapter {
    String getUploadUrl(BucketType bucketName, String objectName);

    String getReadUrl(BucketType bucketName, String objectName);

    String getUpdateUrl(BucketType bucketName, String objectName);


}
