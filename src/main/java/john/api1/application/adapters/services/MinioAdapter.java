package john.api1.application.adapters.services;

import io.minio.MinioClient;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import john.api1.application.components.MinioBucket;
import john.api1.application.components.enums.BucketType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
public class MinioAdapter {
    private final MinioClient minioClient;
    private final MinioBucket minioBucket;

    public MinioAdapter(@Value("${minio.url}") String minioUrl,
                        @Value("${minio.access.key}") String accessKey,
                        @Value("${minio.secret.key}") String secretKey,
                        MinioBucket minioBucket) {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();

        this.minioBucket = minioBucket;
    }

    private String getPreSignedUrl(BucketType bucket, String objectName, Method method) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioBucket.getBucketName(bucket))
                            .object(objectName)
                            .method(method)
                            .expiry(bucket.getMinuteExpire(), TimeUnit.MINUTES)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("MinIO error: " + e.getMessage(), e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating pre-signed URL", e);
        }
    }

    public String getUploadUrl(BucketType bucketName, String objectName) {
        return getPreSignedUrl(bucketName, objectName, Method.PUT);
    }

    public String getReadUrl(BucketType bucketName, String objectName) {
        return getPreSignedUrl(bucketName, objectName, Method.GET);
    }

    public String getUpdateUrl(BucketType bucketName, String objectName) {
        return getPreSignedUrl(bucketName, objectName, Method.PUT);
    }
}
