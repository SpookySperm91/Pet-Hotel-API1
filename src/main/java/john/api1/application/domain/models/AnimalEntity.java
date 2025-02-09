package john.api1.application.domain.models;

import john.api1.application.domain.models.metadata.AnimalMetadata;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.sql.Date;
import java.util.Map;

@Data
public class AnimalEntity {
    @Id
    private ObjectId id;
    private ObjectId clientId;
    private AnimalMetadata animal;
            // hotel, grooming, medical, etc...
    private Map<String, Object> serviceMetadata;
    private Date createAt;
    private Date updateAt;
}
