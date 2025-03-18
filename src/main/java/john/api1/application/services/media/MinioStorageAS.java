package john.api1.application.services.media;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.MinioBucket;
import john.api1.application.components.enums.BucketType;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MinioStorageAS {
    private final MinioClient minioClient;
    private final MinioBucket minioBucket;

    @Autowired
    public MinioStorageAS(MinioClient minioClient, MinioBucket minioBucket) {
        this.minioClient = minioClient;
        this.minioBucket = minioBucket;
    }

    public DomainResponse<String> generatePreSignedUrl(String fileId, String fileName, BucketType bucketType) {
        if (!ObjectId.isValid(fileId)) {
            return DomainResponse.error("Invalid file ID.");
        }

        // Step 1: Generate the object name in MinIO (fileId + fileName)
        String objectName = fileId + "-" + fileName;

        // Step 2: Retrieve the corresponding bucket name based on the bucket type
        String bucketName = minioBucket.getBucketName(bucketType);
        if (bucketName == null) {
            return DomainResponse.error("Invalid bucket type.");
        }

        try {
            // Step 3: Generate the pre-signed URL using MinIO
            String preSignedUrl = generatePreSignedUrlForMinio(bucketName, objectName);

            // Step 4: Return the pre-signed URL wrapped in a success response
            log.info("Generated pre-signed URL: {}", preSignedUrl);
            return DomainResponse.success(preSignedUrl);
        } catch (Exception e) {
            log.error("Error generating pre-signed URL: {}", e.getMessage(), e);
            return DomainResponse.error("Error generating pre-signed URL");
        }
    }


    // Helper method to generate the pre-signed URL using MinIO
    private String generatePreSignedUrlForMinio(String bucketName, String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(60 * 60)  // URL valid for 1 hour
                        .build()
        );
    }

}
