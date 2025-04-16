package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "request_video")
public class VideoRequestEntity {
    @Id
    private ObjectId id;
    private ObjectId requestId;
    private ObjectId ownerId;
    private ObjectId mediaId;
    private String fileName;
    @CreatedDate
    private Instant createdAt;

    public static VideoRequestEntity map(ObjectId requestId, ObjectId ownerId, ObjectId mediaId, String fileName){
        return new VideoRequestEntity(null, requestId, ownerId, mediaId, fileName, Instant.now());
    }

}
