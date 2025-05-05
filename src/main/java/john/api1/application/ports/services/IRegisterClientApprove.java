package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;

public interface IRegisterClientApprove {
    DomainResponse<Void> approvePendingPetOwnerAccount(String id);
}
