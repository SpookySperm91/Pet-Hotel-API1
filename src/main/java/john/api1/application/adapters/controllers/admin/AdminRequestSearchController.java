package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.request.search.RequestSearchDTO;
import john.api1.application.ports.services.request.IRequestSearchAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/request/search")
public class AdminRequestSearchController {
    private final IRequestSearchAdmin searchDashboard;

    @Autowired
    public AdminRequestSearchController(IRequestSearchAdmin searchDashboard) {
        this.searchDashboard = searchDashboard;
    }

    @GetMapping("/all-in-progress")
    public ResponseEntity<DTOResponse<List<RequestSearchDTO>>> searchAllInProgress() {
        var inProgress = searchDashboard.searchAllInProgress();
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        inProgress.getData(),
                        inProgress.getMessage()));

    }

    @GetMapping("/all-pending")
    public ResponseEntity<DTOResponse<List<RequestSearchDTO>>> searchAllPending() {
        var inProgress = searchDashboard.searchAllPending();
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        inProgress.getData(),
                        inProgress.getMessage()));
    }

    @GetMapping("/all-completed")
    public ResponseEntity<DTOResponse<List<RequestSearchDTO>>> searchAllCompleted() {
        var inProgress = searchDashboard.searchAllCompleted();
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        inProgress.getData(),
                        inProgress.getMessage()));
    }

    @GetMapping("/all-rejected")
    public ResponseEntity<DTOResponse<List<RequestSearchDTO>>> searchAllRejected() {
        var inProgress = searchDashboard.searchAllRejected();
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        inProgress.getData(),
                        inProgress.getMessage()));
    }


    @GetMapping("/all/{id}")
    public ResponseEntity<DTOResponse<List<RequestSearchDTO>>> searchAllByOwner(@PathVariable String id) {
        var inProgress = searchDashboard.searchAllByOwnerId(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        inProgress.getData(),
                        inProgress.getMessage()));
    }
}
