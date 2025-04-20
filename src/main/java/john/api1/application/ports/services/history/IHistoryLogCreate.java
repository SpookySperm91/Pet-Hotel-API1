package john.api1.application.ports.services.history;

import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.request.RequestDomain;

public interface IHistoryLogCreate {
    void createActivityLogCompletedRequest(RequestDomain request, String petOwner, String pet);
    void createActivityLogBoarding(BoardingDomain request, String petOwner, String pet);


    // Pet and pet owner creation
    void createActivityLogOwnerRegister(String petOwner);
    void createActivityLogPetRegister(String petOwner, String pet);

}


