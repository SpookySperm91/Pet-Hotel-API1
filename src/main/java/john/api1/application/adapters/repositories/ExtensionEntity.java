package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "boarding-extension")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExtensionEntity {
    @Id
    private ObjectId id;
    private ObjectId requestId;
    private ObjectId boardingId;
    private double additionalPrice;
    private long extendedHours;
    private String description;
    private boolean paid = false;
    private Instant createdAt;
    private Instant updatedAt;
}
