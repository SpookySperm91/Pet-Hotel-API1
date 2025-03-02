package john.api1.application.ports.repositories;

import john.api1.application.adapters.repositories.EmailLogsEntity;
import john.api1.application.domain.models.EmailLogsDomain;

public interface ILogEmailRepository {
    void logEmail(EmailLogsDomain email);
}
