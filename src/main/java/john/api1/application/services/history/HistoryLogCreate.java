package john.api1.application.services.history;

import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.history.IHistoryLogCreateRepository;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryLogCreate implements IHistoryLogCreate {
    private final IHistoryLogCreateRepository createRepository;

    @Autowired
    public HistoryLogCreate(IHistoryLogCreateRepository createRepository) {
        this.createRepository = createRepository;
    }

    @Override
    public void createActivityLogRequest(RequestDomain request, String petOwner, String pet) {
        var domain = ActivityLogDomain.createForRequest(request, ActivityLogType.REQUEST_MANAGEMENT, petOwner, pet);
        saveOrThrow(domain);
    }

    @Override
    public void createActivityLogBoarding(RequestDomain request, String petOwner, String pet) {
        var domain = ActivityLogDomain.createForRequest(request, ActivityLogType.BOARDING_MANAGEMENT, petOwner, pet);
        saveOrThrow(domain);
    }

    @Override
    public void createActivityLogOwnerRegister(String petOwner, String pet) {
        String description = "New pet owner registered";
        var domain = ActivityLogDomain.create(ActivityLogType.PET_OWNER_MANAGEMENT, petOwner, pet, description);
        saveOrThrow(domain);
    }

    @Override
    public void createActivityLogPetRegister(String petOwner, String pet) {
        String description = "New pet added for " + petOwner;
        var domain = ActivityLogDomain.create(ActivityLogType.PET_MANAGEMENT, petOwner, pet, description);
        saveOrThrow(domain);
    }

    // Shared save method with exception handling
    private void saveOrThrow(ActivityLogDomain domain) {
        var saved = createRepository.createNewLog(domain);
        if (saved.isEmpty()) {
            throw new PersistenceHistoryException("Activity log failed to save: " + domain);
        }
    }
}
