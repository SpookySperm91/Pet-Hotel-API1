package john.api1.application.adapters.persistence.entities;

import john.api1.application.components.enums.Status;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Getter
@Document(collection = "client-accounts")
public class ClientEntity {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String referenceCode;

    @Indexed(unique = true)
    private String email;

    @Indexed
    private String phoneNumber;

    @Indexed
    private Status status;

    private boolean accountLock;
    private String hashedPassword;
    private String clientName;
    private String address;
    private List<ObjectId> animalIds;
    private Instant createAt;
    private Instant updateAt;
}
