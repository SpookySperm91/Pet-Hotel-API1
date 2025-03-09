package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.AccountCredentialType;
import john.api1.application.services.user.ClientLoginAS;

public interface ILoginPetOwner {
    DomainResponse<String> login(AccountCredentialType type, String userAccount, String password);

    DomainResponse<Boolean> logout(String userId);
}

