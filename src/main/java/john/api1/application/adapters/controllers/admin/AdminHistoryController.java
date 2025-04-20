package john.api1.application.adapters.controllers.admin;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.history.ActivityLogDTO;
import john.api1.application.ports.services.history.IHistoryLogSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/history")
public class AdminHistoryController {
    private final IHistoryLogSearch historyLogSearch;

    @Autowired
    public AdminHistoryController(IHistoryLogSearch historyLogSearch) {
        this.historyLogSearch = historyLogSearch;
    }

    @GetMapping("/search/recent")
    public ResponseEntity<DTOResponse<ActivityLogDTO>> searchRecentLog() {

        try {
            var recent = historyLogSearch.getRecentLog();

            return recent.map(activityLogDTO -> ResponseEntity.status(HttpStatus.OK)
                            .body(DTOResponse.of(
                                    HttpStatus.OK.value(),
                                    activityLogDTO,
                                    "Ok.")))
                    .orElseGet(() -> buildErrorResponse(HttpStatus.OK, "No recent activity log found"));
        } catch (PersistenceException | NullPointerException e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong with the system. Try again");
        }
    }

    @GetMapping("/search/all")
    public ResponseEntity<DTOResponse<List<ActivityLogDTO>>> searchAll() {
        try {
            var recent = historyLogSearch.getAll();

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
