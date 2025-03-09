package john.api1.application.domain.models;

import john.api1.application.components.enums.SendStatus;
import john.api1.application.components.enums.SmsType;
import john.api1.application.components.exception.InvalidLogException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SmsLogDomain {
    private String id;

    private String ownerId;

    private String phoneNumber;
    private String username;
    private SmsType smsType;
    private String body;

    private SendStatus status;
    private String statusReason;

    private Instant sendAt;
    private Instant updatedAt;


    public static SmsLogDomain createNewLog(String ownerId,
                                            String phoneNumber,
                                            String username,
                                            SmsType smsType,
                                            String body) {

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new InvalidLogException("Phone-number is missing");
        }
        if (username == null || username.isEmpty()) {
            throw new InvalidLogException("Username is missing");
        }
        if (smsType == null) {
            throw new InvalidLogException("SMS type is missing");
        }
        if (body == null || body.isEmpty()) {
            throw new InvalidLogException("Text body is missing");
        }

        return new SmsLogDomain(null,
                ownerId,
                phoneNumber,
                username,
                smsType,
                body,
                SendStatus.PENDING,
                null,
                Instant.now(),
                Instant.now());
    }

    public SmsLogDomain updateTimestamp() {
        return new SmsLogDomain(
                this.id,
                this.ownerId,
                this.phoneNumber,
                this.username,
                this.smsType,
                this.body,
                this.status,
                this.statusReason,
                this.sendAt,
                Instant.now() // Updated timestamp
        );
    }
}
