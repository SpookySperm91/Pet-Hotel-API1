package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.owner.PetOwnerDTO;
import john.api1.application.dto.mapper.owner.PetOwnerListDTO;
import john.api1.application.dto.mapper.owner.PetOwnerPendingDTO;
import john.api1.application.ports.services.IPetOwnerSearchAggregation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/search/pet-owner")
public class AdminPetOwnerSearchController {
    private static final Logger logger = LoggerFactory.getLogger(AdminPetOwnerSearchController.class);

    private final IPetOwnerSearchAggregation ownerSearch;

    @Autowired
    public AdminPetOwnerSearchController(IPetOwnerSearchAggregation ownerSearch) {
        this.ownerSearch = ownerSearch;
    }

    @GetMapping("/all")
    public ResponseEntity<DTOResponse<List<PetOwnerDTO>>> getAllActive() {
        var all = ownerSearch.searchAllActive();
        if (!all.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, all.getMessage());

        logger.info("Active pet-owners successfully fetch");
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        all.getData()));
    }

    @GetMapping("/all-list")
    public ResponseEntity<DTOResponse<List<PetOwnerListDTO>>> getAllActiveAsList() {
        var list = ownerSearch.searchAllActiveAsList();
        if (!list.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, list.getMessage());

        logger.info("Active pet-owners list successfully fetch");
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        list.getData(),
                        list.getMessage()));
    }

    @GetMapping("/recent")
    public ResponseEntity<DTOResponse<PetOwnerDTO>> getRecent() {
        var recent = ownerSearch.searchRecent();
        if (!recent.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, recent.getMessage());

        logger.info("Recently approved pet-owner successfully fetch");
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        recent.getData(),
                        recent.getMessage()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOResponse<PetOwnerDTO>> searchById(@PathVariable String id) {
        var owner = ownerSearch.searchById(id);
        if (!owner.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, owner.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        owner.getData()));
    }


    @GetMapping("/all-pending")
    public ResponseEntity<DTOResponse<List<PetOwnerPendingDTO>>> getAllPending() {
        var allPending = ownerSearch.searchAllPending();

        if (!allPending.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, allPending.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        allPending.getData()));
    }

    @GetMapping("/recent-pending")
    public ResponseEntity<DTOResponse<PetOwnerPendingDTO>> getRecentPending() {
        var recentPending = ownerSearch.searchRecentPending();
        if (!recentPending.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, recentPending.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        recentPending.getData()));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}
