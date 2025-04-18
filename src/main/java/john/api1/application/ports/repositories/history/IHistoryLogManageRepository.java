package john.api1.application.ports.repositories.history;

import john.api1.application.domain.models.ActivityLogDomain;

import java.time.Instant;

public interface IHistoryLogManageRepository {
    void deleteByDate(Instant date);
    void deleteById(String id);
    // ???
    void updateFull(ActivityLogDomain domain);
}
