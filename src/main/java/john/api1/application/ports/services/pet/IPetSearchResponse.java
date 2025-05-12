package john.api1.application.ports.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.pet.PetDetailsDTO;

import java.util.List;

public interface IPetSearchResponse {
    DomainResponse<List<PetDetailsDTO>> searchAll();

    DomainResponse<List<PetDetailsDTO>> searchAllByOwnerId(String id);

    DomainResponse<PetDetailsDTO> searchRecent();

}
