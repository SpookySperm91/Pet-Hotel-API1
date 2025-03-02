package john.api1.application.domain.models;

import john.api1.application.components.enums.EmailStatus;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.exception.InvalidEmailLogException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Getter
public class EmailLogsDomain {
    private String id;

    private String recipientEmail;
    private String recipientUsername;
    private EmailType emailType;
    private String body;

    private EmailStatus status;
    private String errorReason; // important details in-case of error

    private Instant sendAt;
    private Instant updatedAt;

    public EmailLogsDomain(String id,
                           String recipientEmail,
                           String recipientUsername,
                           EmailType emailType,
                           String body,
                           EmailStatus status,
                           String errorReason,
                           Instant sendAt,
                           Instant updatedAt) {
        this.id = id;
        this.recipientEmail = recipientEmail;
        this.recipientUsername = recipientUsername;
        this.body = body;
        this.errorReason = errorReason;
        this.sendAt = sendAt;
        this.updatedAt = updatedAt;

        this.emailType = emailType;
        this.status = status;
    }


    public static EmailLogsDomain createNewLog(String recipientEmail,
                                               String recipientUsername,
                                               EmailType emailType,
                                               String body) {

        if (recipientEmail == null || recipientEmail.isEmpty()) {
            throw new InvalidEmailLogException("Recipient email is missing");
        }
        if (recipientUsername == null || recipientUsername.isEmpty()) {
            throw new InvalidEmailLogException("Recipient username is missing");
        }
        if (emailType == null) {
            throw new InvalidEmailLogException("Email type is missing");
        }
        if (body == null || body.isEmpty()) {
            throw new InvalidEmailLogException("Email body is missing");
        }

        return new EmailLogsDomain(
                null,
                recipientEmail,
                recipientUsername,
                emailType,
                body,
                EmailStatus.PENDING,
                null,
                Instant.now(),
                Instant.now()
        );
    }

    public EmailLogsDomain updateTimestamp() {
        return new EmailLogsDomain(
                this.id,
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
