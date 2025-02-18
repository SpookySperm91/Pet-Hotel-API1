package john.api1.application.domain.cores.custom_returns;

import john.api1.application.domain.models.ClientAccountDomain;

public record CreateNewAccountResult(String rawPassword, ClientAccountDomain account) {
}
