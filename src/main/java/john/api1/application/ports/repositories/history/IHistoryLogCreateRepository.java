package john.api1.application.ports.repositories.history;

import john.api1.application.domain.models.ActivityLogDomain;

import java.util.Optional;

public interface IHistoryLogCreateRepository {
    Optional<String> createNewLog(ActivityLogDomain domain);
}
