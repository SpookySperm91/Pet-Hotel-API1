package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.owner.PetOwnerDTO;
import john.api1.application.dto.mapper.owner.PetOwnerPendingDTO;

import java.util.List;

public interface IPetOwnerSearchAggregation {
    DomainResponse<List<PetOwnerDTO>> searchAllActive();
    DomainResponse<List<PetOwnerPendingDTO>> searchAllPending();
    DomainResponse<PetOwnerDTO> searchRecent();
    DomainResponse<PetOwnerPendingDTO> searchRecentPending();
    DomainResponse<PetOwnerDTO> searchById(String id);
}
