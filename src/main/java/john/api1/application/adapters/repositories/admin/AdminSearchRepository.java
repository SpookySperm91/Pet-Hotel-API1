package john.api1.application.adapters.repositories.admin;

import john.api1.application.adapters.repositories.AdminEntity;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.AdminDomain;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.ports.repositories.admin.IAdminSearchRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AdminSearchRepository implements IAdminSearchRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public AdminSearchRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<AdminDomain> searchById(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid admin ID: cannot convert to ObjectId");

        AdminEntity entity = mongoTemplate.findById(new ObjectId(id), AdminEntity.class);
        return Optional.ofNullable(entity).map(this::map);
    }

    @Override
    public Optional<AdminDomain> searchByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        AdminEntity entity = mongoTemplate.findOne(query, AdminEntity.class);
        return Optional.ofNullable(entity).map(this::map);
    }

    @Override
    public Optional<AdminDomain> searchByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        AdminEntity entity = mongoTemplate.findOne(query, AdminEntity.class);
        return Optional.ofNullable(entity).map(this::map);
    }

    @Override
    public List<AdminDomain> searchAllActive() {
        Query query = new Query(Criteria.where("active").is(true));
        List<AdminEntity> entities = mongoTemplate.find(query, AdminEntity.class);
        return entities.stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private AdminDomain map(AdminEntity entity) {
        return new AdminDomain(
                entity.getId().toHexString(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }
}
