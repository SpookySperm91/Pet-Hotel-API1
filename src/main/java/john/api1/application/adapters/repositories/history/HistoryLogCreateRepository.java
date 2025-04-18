package john.api1.application.adapters.repositories.history;

import john.api1.application.adapters.repositories.ActivityLogEntity;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.ports.repositories.history.IHistoryLogCreateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class HistoryLogCreateRepository implements IHistoryLogCreateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public HistoryLogCreateRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<String> createNewLog(ActivityLogDomain domain) {
        ActivityLogEntity entity = ActivityLogEntity.mapDomain(domain);
        var saved = mongoTemplate.save(entity);

        if (saved.getId() != null) {
            return Optional.of(saved.getId().toHexString());
        }

        return Optional.empty();
    }


}
