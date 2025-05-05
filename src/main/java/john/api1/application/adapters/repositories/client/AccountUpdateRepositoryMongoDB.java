package john.api1.application.adapters.repositories.client;

import com.mongodb.client.result.UpdateResult;
import john.api1.application.adapters.repositories.ClientEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.ports.repositories.owner.IAccountUpdateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;


@Repository
@Qualifier("MongoAccountUpdateRepo")
public class AccountUpdateRepositoryMongoDB implements IAccountUpdateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AccountUpdateRepositoryMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public boolean updateEmail(String id, String newEmail) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update().set("email", newEmail).set("updateAt", Instant.now());
        return updateField(query, update);
    }

    @Override
    public boolean updatePhoneNumber(String id, String newPhoneNumber) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update().set("phoneNumber", newPhoneNumber).set("updateAt", Instant.now());
        return updateField(query, update);
    }

    @Override
    public boolean updatePassword(String id, String newPassword) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update().set("hashedPassword", newPassword).set("updateAt", Instant.now());
        return updateField(query, update);
    }

    @Override
    public void updateAccount(ClientAccountDomain accountDomain) {
        if (accountDomain.getId() == null || !ObjectId.isValid(accountDomain.getId()))
            throw new PersistenceException("Invalid account id cannot be converted to ObjectId or currently empty");

        ObjectId objectId = new ObjectId(accountDomain.getId());
        Query query = new Query(Criteria.where("_id").is(objectId));

        Update update = new Update()
                .set("email", accountDomain.getEmail())
                .set("phoneNumber", accountDomain.getPhoneNumber())
                .set("hashedPassword", accountDomain.getHashedPassword())
                .set("accountLock", accountDomain.isLocked())
                .set("updateAt", accountDomain.getUpdatedAt());

        if (!updateField(query, update)) {
            throw new PersistenceException("Account update failed for id: " + accountDomain.getId());
        }

    }


    private boolean updateField(Query query, Update update) {
        UpdateResult result = mongoTemplate.updateFirst(query, update, ClientEntity.class);
        return result.getModifiedCount() > 0;
    }
}
