package john.api1.application.domain.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import john.api1.application.components.enums.EntityType;
import john.api1.application.components.enums.PhotoDocumentType;
import john.api1.application.domain.models.metadata.PhotoMetadata;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
public class PhotoEntity {
    @Id
    @JsonIgnore
    private ObjectId id;
    private ObjectId entityId;
    private String entityType;
    private PhotoDocumentType photoDocumentType;
    private PhotoMetadata photo;
}
