package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "client_accounts")
@CompoundIndexes({
        @CompoundIndex(name = "idx_animalIds", def = "{'animalIds': 1}")
})
public class ClientEntity {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String email;

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



