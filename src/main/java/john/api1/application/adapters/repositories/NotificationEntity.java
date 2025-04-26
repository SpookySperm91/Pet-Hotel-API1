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
@Document(collection = "owners_notification")
public class NotificationEntity {
    @Id
    private ObjectId id;
    private ObjectId requestId;
    private ObjectId ownerId;
    private String description;
    private String notificationType;
    @CreatedDate
    private Instant createdAt;
    private boolean read;
}
