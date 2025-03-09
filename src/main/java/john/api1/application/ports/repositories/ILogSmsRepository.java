package john.api1.application.ports.repositories;

import john.api1.application.domain.models.SmsLogDomain;

public interface ILogSmsRepository {
    void logSmsText(SmsLogDomain smsLogDomain);
}
