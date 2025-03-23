package john.api1.application.adapters.repositories.client;

import john.api1.application.adapters.repositories.ClientEntity;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.ports.repositories.account.IAccountSearchRepository;
import john.api1.application.ports.repositories.wrapper.ClientFullAccount;
import john.api1.application.ports.repositories.wrapper.UsernameAndId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Qualifier("MongoAccountSearchRepo")
public class AccountSearchRepositoryMongoDB implements IAccountSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountSearchRepositoryMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<ClientAccountDomain> getAccountById(String id) {
        if (!ObjectId.isValid(id)) return Optional.empty();

        return Optional.ofNullable(mongoTemplate.findById(new ObjectId(id), ClientEntity.class))
                .map(this::mapToClientAccount);
    }

    @Override
    public Optional<ClientAccountDomain> getAccountByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return Optional.ofNullable(mongoTemplate.findOne(query, ClientEntity.class))
                .map(this::mapToClientAccount);
    }

    @Override
    public Optional<ClientAccountDomain> getAccountByPhoneNumber(String phoneNumber) {
        Query query = new Query(Criteria.where("phoneNumber").is(phoneNumber));
        return Optional.ofNullable(mongoTemplate.findOne(query, ClientEntity.class))
                .map(this::mapToClientAccount);
    }

    @Override
    public List<ClientAccountDomain> getAllAccount() {
        return mongoTemplate.findAll(ClientEntity.class)
                .stream()
                .map(this::mapToClientAccount)
                .collect(Collectors.toList());
    }

    // ReadOnly
    @Override
    public Optional<UsernameAndId> getUsernameIdByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        query.fields().include("clientName").include("_id");
        ClientEntity result = mongoTemplate.findOne(query, ClientEntity.class);

        return Optional.ofNullable(result)
                .map(client -> new UsernameAndId(client.getClientName(), client.getId().toHexString()));
    }

    // Full object
    @Override
    public Optional<ClientFullAccount> getFullAccountById(String id) {
        if (!ObjectId.isValid(id)) return Optional.empty();

        return Optional.ofNullable(mongoTemplate.findById(new ObjectId(id), ClientEntity.class))
                .map(client -> new ClientFullAccount(
                        mapToClientAccount(client),
                        mapToClientProfile(client)
                ));
    }


    private ClientAccountDomain mapToClientAccount(ClientEntity account) {
        return new ClientAccountDomain(
                account.getId().toString(),
                account.getEmail(),
                account.getPhoneNumber(),
                account.getHashedPassword(),
                account.isAccountLock());
    }

    private ClientDomain mapToClientProfile(ClientEntity client) {
        return new ClientDomain(
                client.getId().toString(),
                client.getClientName(),
                // List<String> owner's pet id
                client.getAnimalIds() != null
                        ? client.getAnimalIds().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
                        : Collections.emptyList(),
                //////////////////////////
                client.getStreetAddress(),
                client.getCityAddress(),
                client.getStateAddress(),
                client.getEmergencyNumber(),
                client.getCreateAt(),
                client.getUpdateAt()
        );
    }

}
