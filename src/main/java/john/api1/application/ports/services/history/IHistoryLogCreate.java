package john.api1.application.ports.services.history;

import john.api1.application.domain.models.request.RequestDomain;

public interface IHistoryLogCreate {
    void createActivityLogRequest(RequestDomain request, String petOwner, String pet);
    void createActivityLogBoarding(RequestDomain request, String petOwner, String pet);


    // Pet and pet owner creation
    void createActivityLogOwnerRegister(String petOwner, String pet);
    void createActivityLogPetRegister(String petOwner, String pet);

}


