package john.api1.application.adapters.repositories.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "failed-emails")
public class FailedEmailEntity {
    @Id
    private ObjectId id;

    private String recipientEmail;
    private String recipientUsername;
    private String emailType;
    private String body;

    private Instant failedAt;
    private Instant updatedAt;
    private boolean resend;
    private String errorMessage;
}
