package john.api1.application.adapters.persistence;

import john.api1.application.adapters.persistence.entities.ClientEntity;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.ports.persistence.IAccountSearchRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountSearchRepositoryMongoDB implements IAccountSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountSearchRepositoryMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<ClientAccountDomain> getAccountById(String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            return Optional.ofNullable(mongoTemplate.findById(objectId, ClientEntity.class))
                    .map(this::mapping);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ClientAccountDomain> getAccountByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return Optional.ofNullable(mongoTemplate.findOne(query, ClientEntity.class))
                .map(this::mapping);
    }

    @Override
    public Optional<ClientAccountDomain> getAccountByPhoneNumber(String phoneNumber) {
        Query query = new Query(Criteria.where("phoneNumber").is(phoneNumber));
        return Optional.ofNullable(mongoTemplate.findOne(query, ClientEntity.class))
                .map(this::mapping);
    }

    @Override
    public List<ClientAccountDomain> getAllAccount() {
        return mongoTemplate.findAll(ClientEntity.class)
                .stream()
                .map(this::mapping)
                .toList();
    }


    private ClientAccountDomain mapping(ClientEntity account) {
        return new ClientAccountDomain(
                account.getId().toString(),
                account.getEmail(),
                account.getPhoneNumber(),
                account.getHashedPassword(),
                account.isAccountLock());
    }
}
