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
@Document(collection = "${db.collection.email.log}")
public class EmailLogsEntity {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId ownerId;

    @Indexed
    private String recipientEmail;
    private String recipientUsername;
    private String emailType;
    private String body;

    @Indexed
    private String status;
    private String errorReason; // important details in-case of error

    @Indexed
    private Instant sendAt;
    private Instant updatedAt;
}
