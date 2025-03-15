package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.PetRequestDTO;

public interface IPetRegister {
    DomainResponse<String> registerPet(PetRequestDTO petRegister);
}
