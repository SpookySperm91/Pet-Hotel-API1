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
@Document(collection = "sms_logs")
public class SmsLogEntity {
    @Id
    private ObjectId id;

    private ObjectId ownerId;

    private String username;
    @Indexed
    private String phoneNumber;
    private String smsType;
    private String body;

    private String status;
    private String statusReason;

    @Indexed
    private Instant sendAt;
    private Instant updatedAt;
}
