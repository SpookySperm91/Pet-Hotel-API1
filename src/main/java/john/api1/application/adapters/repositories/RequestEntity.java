package john.api1.application.adapters.repositories;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "request")
@CompoundIndexes({
        @CompoundIndex(name = "ownerId_idx", def = "{'ownerId': 1}"),
        @CompoundIndex(name = "petId_idx", def = "{'petId': 1}"),
        @CompoundIndex(name = "boardingId_idx", def = "{'boardingId': 1}"),
        @CompoundIndex(
                name = "recent_media_request_idx",
                def = "{'requestType': 1, 'active': 1, 'updatedAt': -1}"
        )
})
public class RequestEntity {
    @Id
    private ObjectId id;
    private ObjectId ownerId;
    private ObjectId petId;
    private ObjectId boardingId;
    private String requestType;
    private String requestStatus;
    private String description;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @Nullable
    private String responseMessage;
    private boolean active = true;

    public static RequestEntity create(ObjectId ownerId, ObjectId petId, ObjectId boardingId, String requestType, String requestStatus, String description, Instant createdAt) {
        return new RequestEntity(
                null, ownerId, petId, boardingId, requestType, requestStatus, description, createdAt, Instant.now(), null, true
        );
    }
}
