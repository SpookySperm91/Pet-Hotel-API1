package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.ProfileResponseDTO;

public interface IPetProfilePhoto {
    DomainResponse<ProfileResponseDTO> processProfilePhoto(String id, String petName);
}
