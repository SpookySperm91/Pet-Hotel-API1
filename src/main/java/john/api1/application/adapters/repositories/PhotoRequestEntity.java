package john.api1.application.adapters.repositories;

import john.api1.application.domain.models.request.PhotoRequestDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "request_photo")
public class PhotoRequestEntity {
    @Id
    private ObjectId id;
    private ObjectId requestId;
    private ObjectId ownerId;
    private List<PhotoFileListEnt> photoFile;
    @CreatedDate
    private Instant createdAt;

    public static PhotoRequestEntity create(ObjectId requestId, ObjectId ownerId, List<PhotoFileListEnt> photoFile) {
        return new PhotoRequestEntity(null, requestId, ownerId, photoFile, Instant.now());
    }

    public static List<PhotoFileListEnt> map(List<PhotoRequestDomain.MediaFile> media) {
        return media.stream()
                .map(m -> new PhotoFileListEnt(new ObjectId(m.id()), m.fileName()))
                .toList();
    }

    public record PhotoFileListEnt(
            ObjectId mediaId,
            String fileName
    ) {

    }
}
