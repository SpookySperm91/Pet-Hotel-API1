package john.api1.application.adapters.repositories.admin;

import john.api1.application.adapters.repositories.AdminEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.ports.repositories.admin.IAdminCreateRepository;
import john.api1.application.ports.repositories.admin.IAdminManageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdminManageRepository implements IAdminCreateRepository, IAdminManageRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AdminManageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<String> create(AdminDomain domain) {
        AdminEntity entity = map(domain);
        AdminEntity saved = mongoTemplate.insert(entity);
        return Optional.ofNullable(saved.getId()).map(ObjectId::toHexString);
    }

    @Override
    public void updateAdmin(AdminDomain domain) {
        if (!ObjectId.isValid(domain.getId()))
            throw new PersistenceException("Invalid admin id cannot be converted to ObjectId");

        ObjectId objectId = new ObjectId(domain.getId());
        AdminEntity entity = map(domain);
        entity.setId(objectId);
        mongoTemplate.save(entity); // upsert based on ID
    }

    @Override
    public void deleteAdminById(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid admin id cannot be converted to ObjectId");

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        mongoTemplate.remove(query, AdminEntity.class);
    }

    @Override
    public long deleteInactiveAdmin() {
        Query query = new Query(Criteria.where("active").is(false));
        return mongoTemplate.remove(query, AdminEntity.class).getDeletedCount();
    }

    private AdminEntity map(AdminDomain domain) {
        return new AdminEntity(
                null,
                domain.getUsername(),
                domain.getEmail(),
                domain.getPassword(),
                domain.isActive(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }


}
