package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.pet.PetDetailsDTO;
import john.api1.application.ports.services.pet.IPetSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/search/pet")
public class AdminPetSearchController {
    private final IPetSearchResponse search;

    @Autowired
    public AdminPetSearchController(IPetSearchResponse search) {
        this.search = search;
    }

    @GetMapping("/all")
    public ResponseEntity<DTOResponse<List<PetDetailsDTO>>> searchAll() {
        var all = search.searchAll();

        if (!all.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, all.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        all.getData(),
                        all.getMessage()));
    }

    @GetMapping("/recent")
    public ResponseEntity<DTOResponse<PetDetailsDTO>> searchRecent() {
        var all = search.searchRecent();

        if (!all.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, all.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        all.getData(),
                        all.getMessage()));
    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
