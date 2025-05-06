package john.api1.application.adapters.repositories.client;

import john.api1.application.adapters.repositories.ClientEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.ports.repositories.owner.IAccountSearchRepository;
import john.api1.application.ports.repositories.owner.IPetOwnerCQRSRepository;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.wrapper.ClientFullAccount;
import john.api1.application.ports.repositories.wrapper.UsernameAndId;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
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
public class AccountSearchRepositoryMongoDB implements IAccountSearchRepository, IPetOwnerCQRSRepository {
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
        Query query = new Query(
                Criteria.where("email").is(email));
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

    // Check if exist
    public boolean existById(String id) {
        if (!ObjectId.isValid(id)) return false;


        return mongoTemplate.exists(
                Query.query(Criteria.where("_id").is(new ObjectId(id))),
                ClientEntity.class
        );
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


    // CQRS METHODS
    //
    @Override
    public Optional<PetOwnerCQRS> getDetails(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Pet-owner id cannot be mapped to ObjectId");

        return Optional.ofNullable(mongoTemplate.findById(new ObjectId(id), ClientEntity.class))
                .map(this::mapToCQRS);
    }

    @Override
    public Optional<List<String>> getAllPets(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Pet-owner id cannot be mapped to ObjectId");

        return Optional.ofNullable(mongoTemplate.findById(new ObjectId(id), ClientEntity.class))
                .map(ClientEntity::getAnimalIds)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream().map(ObjectId::toString).toList());
    }

    @Override
    public Optional<String> checkPetIfExist(String ownerId, String petId) {
        if (!ObjectId.isValid(ownerId) || !ObjectId.isValid(petId))
            throw new PersistenceException("Pet-owner or pet id cannot be mapped to ObjectId");

        return Optional.ofNullable(mongoTemplate.findById(new ObjectId(ownerId), ClientEntity.class)
                ).filter(client -> client.getAnimalIds() != null && client.getAnimalIds().contains(new ObjectId(petId)))
                .map(client -> petId); // return the found petId as String
    }

    @Override
    public Optional<String> getPetOwnerName(String ownerId) {
        if (!ObjectId.isValid(ownerId)) throw new PersistenceException("Pet-owner id cannot be mapped to ObjectId");

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(ownerId)));
        query.fields().include("clientName");

        return Optional.ofNullable(mongoTemplate.findOne(query, ClientEntity.class))
                .map(ClientEntity::getClientName);
    }


    @Override
    public List<PetOwnerCQRS> getAllActive() {
        Query query = new Query();
        query.addCriteria(Criteria.where("accountLock").is(false));
        var clients = mongoTemplate.find(query, ClientEntity.class);

        return clients.stream()
                .map(this::mapToCQRS)
                .toList();
    }

    @Override
    public Optional<PetOwnerCQRS> getRecentActive() {
        Query query = new Query();
        query.addCriteria(Criteria.where("accountLock").is(false));
        query.with(Sort.by(Sort.Direction.DESC, "createAt"));
        query.limit(1);

        ClientEntity entity = mongoTemplate.findOne(query, ClientEntity.class);
        return Optional.ofNullable(entity)
                .map(this::mapToCQRS);
    }

    @Override
    public List<PetOwnerCQRS> getAllPending() {
        Query query = new Query();
        query.addCriteria(Criteria.where("accountLock").is(true));
        var clients = mongoTemplate.find(query, ClientEntity.class);

        return clients.stream()
                .map(this::mapToCQRS)
                .toList();
    }

    @Override
    public Optional<PetOwnerCQRS> getRecentPending() {
        Query query = new Query();
        query.addCriteria(Criteria.where("accountLock").is(true));
        query.with(Sort.by(Sort.Direction.DESC, "createAt"));
        query.limit(1);

        ClientEntity entity = mongoTemplate.findOne(query, ClientEntity.class);
        return Optional.ofNullable(entity)
                .map(this::mapToCQRS);
    }


    private PetOwnerCQRS mapToCQRS(ClientEntity account) {
        return new PetOwnerCQRS(
                account.getId().toString(),
                account.getClientName(),
                account.getEmail(),
                account.getPhoneNumber(),
                account.getStreetAddress(),
                account.getCityAddress(),
                account.getStateAddress(),
                account.getCreateAt());
    }
}
