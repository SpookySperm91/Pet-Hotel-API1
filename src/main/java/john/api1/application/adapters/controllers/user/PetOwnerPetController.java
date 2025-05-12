package john.api1.application.adapters.controllers.user;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.pet.PetUpdatedDTO;
import john.api1.application.dto.request.PetChangeRDTO;
import john.api1.application.ports.repositories.pet.IPetSearchRepository;
import john.api1.application.ports.services.pet.IPetUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pet-owner/pets/update")
public class PetOwnerPetController {
    private final IPetSearchRepository petSearch;
    private final IPetUpdate petUpdate;

    @Autowired
    public PetOwnerPetController(IPetSearchRepository petSearch,
                                 IPetUpdate petUpdate) {
        this.petSearch = petSearch;
        this.petUpdate = petUpdate;
    }

    @PutMapping("/profile")
    public ResponseEntity<DTOResponse<PetUpdatedDTO>> editPet(@RequestBody PetChangeRDTO request) {
        if (request.getPetId() == null || request.getPetId().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing pet id");
        }
        if (request.getOwnerId() == null || request.getOwnerId().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing owner id");
        }

        var searchPet = petSearch.getPetById(request.getPetId());
        if (searchPet.isEmpty()) return buildErrorResponse(HttpStatus.BAD_REQUEST, "Pet cannot be found");

        var domain = searchPet.get();

        if (request.getPetName() != null) {
            domain.updatePetName(request.getPetName().trim());
        }

        if (request.getBreed() != null) {
            domain.updateBreed(request.getBreed().trim());

        }

        if (request.getSize() != null) {
            domain.updateSize(request.getSize().trim());

        }

        if (request.getAge() != null && request.getAge() >= 0) {
            domain.updateAge(request.getAge());
        }

        if (request.getProfilePhoto() == null || request.getProfilePhoto().isEmpty()) {
            request.setProfilePhoto("");
        }


        var save = petUpdate.updatePet(domain, request.getProfilePhoto());
        if (!save.isSuccess()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, save.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                DTOResponse.of(HttpStatus.OK.value(), save.getData(), "Account successfully updated")
        );
    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
