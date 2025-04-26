package john.api1.application.adapters.repositories;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "storage_files")
@CompoundIndexes({
        @CompoundIndex(name = "ownerId_idx", def = "{'ownerId': 1}"),
        @CompoundIndex(name = "typeId_idx", def = "{'typeId': 1}")
})
public class MinioEntity {
    @Id
    private ObjectId id;
    private ObjectId ownerId;
    @Field("typeId")
    @Nullable
    private ObjectId typeId; // request id (nullable for non-request related photos)
    private String fileName;  // name + id
    private String bucketType; // bucket type(request type)
    private String description;
    private Instant uploadedAt;
    private Instant preSignedUrlExpire;
    private boolean archived = false;
}
