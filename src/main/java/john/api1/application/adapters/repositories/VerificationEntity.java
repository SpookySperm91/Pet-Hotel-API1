package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "verification_requests")
public class VerificationEntity {
    @Id
    private ObjectId id;
    private ObjectId associatedId; // owner's id
    private String associatedUsername;
    @Indexed
    private String verificationType;
    @Indexed(unique = true)
    private String value;
    private boolean used;
    private Instant createdAt;
    @Indexed(expireAfter  = "30m")
    private Instant expireAt;
}
