package john.api1.application.ports.repositories.wrapper;

import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;

public record ClientFullAccount(
        ClientAccountDomain clientAccount,
        ClientDomain clientProfile
) {
}
