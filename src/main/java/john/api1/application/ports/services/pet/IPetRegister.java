package john.api1.application.ports.services.pet;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.PetRDTO;

public interface IPetRegister {
    DomainResponse<String> registerPet(PetRDTO petRegister);
}
