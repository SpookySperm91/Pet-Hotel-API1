package john.api1.application.adapters.services;

import john.api1.application.components.DigitalOceanS3Folder;
import john.api1.application.components.enums.BucketType;
import john.api1.application.ports.services.media.IMediaAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@Qualifier("DigitalOceanS3Adapter")
public class DigitalOceanS3Adapter implements IMediaAdapter {
    private final DigitalOceanS3Folder bucketMapper;
    private final S3Presigner preSigner;

    @Value("${digital-ocean.bucket.name}")
    private String bucketName;

    @Value("${digital-ocean.url}")
    private String cdnEndpoint;
    @Autowired
    public DigitalOceanS3Adapter(DigitalOceanS3Folder bucketMapper,
                                 S3Presigner preSigner) {
        this.bucketMapper = bucketMapper;
        this.preSigner = preSigner;
    }

    private String generatePreSignedUrl(BucketType bucketType, String objectName, String method) {
        String folder = bucketMapper.getBucketName(bucketType);  // This should return something like "request-photo"
        String key = folder + "/" + objectName;  // This should form "request-photo/example-file"
        Duration expiration = Duration.ofMinutes(bucketType.getMinuteExpire());

        return switch (method.toUpperCase()) {
            case "GET" -> {
                GetObjectPresignRequest getRequest = GetObjectPresignRequest.builder()
                        .getObjectRequest(r -> r.bucket(bucketName).key(key))
                        .signatureDuration(expiration)
                        .build();
                String rawUrl = preSigner.presignGetObject(getRequest).url().toString();

                // Replace the raw endpoint with the CDN endpoint
                yield rawUrl.replaceFirst("^https://[^/]+", cdnEndpoint);
            }
            case "PUT" -> {
                PutObjectPresignRequest putRequest = PutObjectPresignRequest.builder()
                        .putObjectRequest(r -> r.bucket(bucketName).key(key))
                        .signatureDuration(expiration)
                        .build();
                String f = preSigner.presignPutObject(putRequest).url().toString();
                System.out.println("URL" +  f);
                yield f;
            }
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }


    // Public methods to retrieve pre-signed URLs for specific actions
    public String getUploadUrl(BucketType bucketType, String objectName) {
        return generatePreSignedUrl(bucketType, objectName, "PUT");
    }

    public String getReadUrl(BucketType bucketType, String objectName) {
        return generatePreSignedUrl(bucketType, objectName, "GET");
    }

    public String getUpdateUrl(BucketType bucketType, String objectName) {
        return generatePreSignedUrl(bucketType, objectName, "PUT");
    }
}
