package john.api1.application.adapters.repositories;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "${db.collection.request.grooming}")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroomingEntity {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId requestId;
    @Indexed
    private ObjectId boardingId;
    private String groomingType;
    private double groomingPrice;
    @Nullable
    private String description;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean approved;

    public static GroomingEntity create(ObjectId requestId, ObjectId boardingId, String serviceType, double groomingPrice, String description, Instant createdAt, Instant updatedAt, boolean approved) {
        return new GroomingEntity(null, requestId, boardingId, serviceType, groomingPrice, description, createdAt, updatedAt, approved);
    }
}


