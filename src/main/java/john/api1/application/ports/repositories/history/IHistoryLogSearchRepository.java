package john.api1.application.ports.repositories.history;

import john.api1.application.adapters.repositories.ActivityLogEntity;
import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.domain.models.ActivityLogDomain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IHistoryLogSearchRepository {
    Optional<ActivityLogDomain> searchById(String id);
    Optional<ActivityLogDomain> searchRecently();
    List<ActivityLogDomain> searchAll();
    List<ActivityLogDomain> searchByDate(Instant date);
    List<ActivityLogDomain> searchByActivityType(ActivityLogType type);
}
