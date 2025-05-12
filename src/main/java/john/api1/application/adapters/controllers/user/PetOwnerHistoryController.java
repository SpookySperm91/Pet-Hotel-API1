package john.api1.application.adapters.controllers.user;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.history.ActivityLogDTO;
import john.api1.application.dto.mapper.history.media.MediaHistoryDTO;
import john.api1.application.ports.services.history.IHistoryLogSearch;
import john.api1.application.ports.services.history.IHistoryMediaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pet-owner/history")
public class PetOwnerHistoryController {
    private final IHistoryLogSearch historyLogSearch;
    private final IHistoryMediaSearch historyMediaSearch;

    @Autowired
    public PetOwnerHistoryController(IHistoryMediaSearch historyMediaSearch, IHistoryLogSearch historyLogSearch) {
        this.historyMediaSearch = historyMediaSearch;
        this.historyLogSearch =historyLogSearch;
    }


    @GetMapping("/search/media/recent")
    public ResponseEntity<DTOResponse<MediaHistoryDTO>> searchRecentMedia() {
        // Get recent media
        // Medias source is based on per request
        // Similar to request format
        try {
            var recent = historyMediaSearch.getRecentHistoryMedia();
            return recent.map(mediaHistoryDTO -> ResponseEntity.ok(DTOResponse.of(
                            HttpStatus.OK.value(),
                            mediaHistoryDTO,
                            "Ok.")))
                    .orElseGet(() -> buildErrorResponse(HttpStatus.OK, "No recent activity log found"));
        } catch (PersistenceException | PersistenceHistoryException | NullPointerException e) {
            return buildErrorResponse(HttpStatus.OK, "No recent activity log found");
        }
    }

    @GetMapping("/search/media/all")
    public ResponseEntity<DTOResponse<List<ActivityLogDTO>>> searchAllMedia() {
        try {
            var recent = historyLogSearch.getAllMedia();

            if (!recent.isEmpty()) {
                return ResponseEntity.ok(DTOResponse.of(
                        HttpStatus.OK.value(),
                        recent,
                        "Ok."));
            } else {
                return buildErrorResponse(HttpStatus.OK, "No recent activity log found");
            }
        } catch (PersistenceException | NullPointerException e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong with the system. Try again");
        }
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}
