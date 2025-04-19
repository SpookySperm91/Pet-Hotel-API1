package john.api1.application.ports.services.history;

import john.api1.application.adapters.repositories.ActivityLogEntity;
import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.dto.mapper.history.ActivityLogDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface IHistoryLogSearch {
    Optional<ActivityLogDTO> getRecentLog();

    List<ActivityLogDTO> getAll();

    Optional<ActivityLogDTO> searchById(String id);

    Optional<List<ActivityLogDTO>> getByDate(Instant time);

    Optional<List<ActivityLogDTO>> getByBetweenDate(Instant start, Instant end);

    List<ActivityLogDTO> searchByActivityType(ActivityLogType type);

}
