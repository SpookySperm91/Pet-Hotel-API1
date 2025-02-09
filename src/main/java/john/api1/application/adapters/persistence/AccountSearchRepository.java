package john.api1.application.adapters.persistence;

import john.api1.application.domain.ports.persistence.IAccountSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountSearchRepository<T, ID> implements IAccountSearchRepository<T, ID> {
    private final MongoTemplate mongoTemplate;
    private final Class<T> entityClass;

    @Autowired
    public AccountSearchRepository(MongoTemplate mongoTemplate, Class<T> entityClass) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> getAccountById(ID id) {
        return Optional.of(mongoTemplate.findById(id, entityClass));
    }

    @Override
    public Optional<T> getAccountByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return Optional.of(mongoTemplate.findOne(query, entityClass));
    }

    @Override
    public Optional<T> getAccountByPhoneNumber(String phoneNumber) {
        Query query = new Query(Criteria.where("phoneNumber").is(phoneNumber));
        return Optional.of(mongoTemplate.findOne(query, entityClass));
    }

    @Override
    public List<T> getAllAccount() {
        return mongoTemplate.findAll(entityClass);
    }
}
