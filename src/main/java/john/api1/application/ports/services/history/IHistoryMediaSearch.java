package john.api1.application.ports.services.history;

import john.api1.application.dto.mapper.history.media.MediaHistoryDTO;

import java.util.List;
import java.util.Optional;

public interface IHistoryMediaSearch {
    Optional<MediaHistoryDTO> getRecentHistoryMedia();

    List<MediaHistoryDTO> getAllHistoryMedia();
}
