package john.api1.application.ports.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.ProfileDTO;

public interface IPetProfilePhoto {
    DomainResponse<ProfileDTO> processProfilePhoto(String id, String petName);
}
