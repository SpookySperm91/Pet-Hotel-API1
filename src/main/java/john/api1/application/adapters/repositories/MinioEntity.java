package john.api1.application.adapters.repositories;

import john.api1.application.components.enums.BucketType;
import john.api1.application.components.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "storage_files")
public class MinioEntity {
    @Id
    private ObjectId id;
    private ObjectId ownerId;
    private String fileName;
    private BucketType bucketType;
    private String minioUrl;
    private Instant uploadedAt;
    private Instant preSignedUrlExpire;
    private MediaType mediaType;
}
