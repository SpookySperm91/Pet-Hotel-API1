package john.api1.application.adapters.repositories.client;

import com.mongodb.MongoException;
import john.api1.application.adapters.repositories.ClientEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.ports.repositories.account.IAccountCreateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("MongoCreateRepo")
public class ClientCreationRepositoryMongoDB implements IAccountCreateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public ClientCreationRepositoryMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String createNewClient(ClientAccountDomain newAccount, ClientDomain newClient) {
        try {
            ClientEntity clientEntity = new ClientEntity(
                    null,
                    // account
                    newAccount.getEmail(),
                    newAccount.getPhoneNumber(),
                    newAccount.isLocked(),
                    newAccount.getHashedPassword(),
                    // information
                    newClient.getFullName(),
                    newClient.getStreetAddress(),
                    newClient.getCityAddress(),
                    newClient.getStateAddress(),
                    newClient.getEmergencyPhoneNumber(),
                    newClient.getValidPetObjectIds(),
                    newClient.getCreatedAt(),
                    newClient.getUpdatedAt()
            );
            return mongoTemplate.save(clientEntity).getId().toString();
        } catch (MongoException e) {
            throw new PersistenceException("MongoDB Database error: Failed to save client to database", e);
        }
    }
}