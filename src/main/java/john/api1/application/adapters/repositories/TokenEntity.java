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
@Document(collection = "token_apis")
public class TokenEntity {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String token;
    private String authorizedId;
    private String endpoint;
    private Instant createdAt;
    @Indexed(expireAfter = "10m")
    private Instant expiredAt;
    private boolean used;
}
