package john.api1.application.domain.models;

import john.api1.application.components.enums.SendStatus;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.exception.InvalidLogException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EmailLogsDomain {
    private String id;

    private String ownerId;

    private String recipientEmail;
    private String recipientUsername;
    private EmailType emailType;
    private String body;

    private SendStatus status;
    private String errorReason; // important details in-case of error

    private Instant sendAt;
    private Instant updatedAt;


    public static EmailLogsDomain createNewLog(String ownerId,
                                               String recipientEmail,
                                               String recipientUsername,
                                               EmailType emailType,
                                               String body) {

        if (recipientEmail == null || recipientEmail.isEmpty()) {
            throw new InvalidLogException("Recipient email is missing");
        }
        if (recipientUsername == null || recipientUsername.isEmpty()) {
            throw new InvalidLogException("Recipient username is missing");
        }
        if (emailType == null) {
            throw new InvalidLogException("Email type is missing");
        }
        if (body == null || body.isEmpty()) {
            throw new InvalidLogException("Email body is missing");
        }

        return new EmailLogsDomain(
                null,
                ownerId,
                recipientEmail,
                recipientUsername,
                emailType,
                body,
                SendStatus.PENDING,
                null,
                Instant.now(),
                Instant.now()
        );
    }

    public EmailLogsDomain updateTimestamp() {
        return new EmailLogsDomain(
                this.id,
                this.ownerId,
                this.recipientEmail,
                this.recipientUsername,
                this.emailType,
                this.body,
                this.status,
                this.errorReason,
                this.sendAt,
                Instant.now() // Updated timestamp
        );
    }

}
