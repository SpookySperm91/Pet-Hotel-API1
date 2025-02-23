package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "client-accounts")
public class ClientEntity {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String email;

    @Indexed
    private String phoneNumber;

    private boolean accountLock;
    private String hashedPassword;
    private String clientName;
    private String streetAddress;
    private String cityAddress;
    private String stateAddress;
    private String emergencyNumber;
    private List<ObjectId> animalIds;
    private Instant createAt;
    private Instant updateAt;
}
