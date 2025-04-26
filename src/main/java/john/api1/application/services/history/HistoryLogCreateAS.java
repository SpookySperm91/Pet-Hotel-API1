package john.api1.application.services.history;

import com.mongodb.MongoException;
import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.history.IHistoryLogCreateRepository;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = {DomainArgumentException.class, PersistenceException.class, MongoException.class})
public class HistoryLogCreateAS implements IHistoryLogCreate {
    private final IHistoryLogCreateRepository createRepository;

    @Autowired
    public HistoryLogCreateAS(IHistoryLogCreateRepository createRepository) {
        this.createRepository = createRepository;
    }

    @Override
    public void createActivityLogCompletedRequest(RequestDomain request, String petOwner, String pet) {
        String description = switch (request.getRequestType()) {
            case RequestType.PHOTO_REQUEST -> "Photo request completed";
            case RequestType.VIDEO_REQUEST -> "Video request completed";
            case RequestType.GROOMING_SERVICE -> "Grooming request completed";
            case RequestType.BOARDING_EXTENSION -> "Boarding extension request completed";
            default -> "Request activity log";
        };

        var domain = ActivityLogDomain.createForRequest(request, ActivityLogType.REQUEST_MANAGEMENT, description, petOwner, pet);
        saveOrThrow(domain);
    }

    @Override
    public void createActivityLogBoarding(BoardingDomain request, String petOwner, String pet) {
        String description = switch (request.getBoardingStatus()) {
            case BoardingStatus.BOARDING -> "Pet check in for boarding";
            case BoardingStatus.RELEASED -> "Pet released from boarding";
            default -> "Pet boarding activity log";
        };
        var domain = ActivityLogDomain.createForBoarding(request, ActivityLogType.BOARDING_MANAGEMENT, petOwner, pet, description);
        saveOrThrow(domain);
    }

    @Override
    public void createActivityLogOwnerRegister(String petOwner) {
        String description = "New pet owner registered";
        var domain = ActivityLogDomain.create(ActivityLogType.PET_OWNER_MANAGEMENT, petOwner, null, description);
        saveOrThrow(domain);
    }

    @Override
    public void createActivityLogPetRegister(String petOwner, String pet) {
        String description = "New pet added";
        var domain = ActivityLogDomain.create(ActivityLogType.PET_MANAGEMENT, petOwner, pet, description);
        saveOrThrow(domain);
    }

    // Shared save method with exception handling
    private void saveOrThrow(ActivityLogDomain domain) {
        try {
            var saved = createRepository.createNewLog(domain);
            if (saved.isEmpty()) {
                throw new PersistenceHistoryException("Activity log failed to save: " + domain);
            }
        } catch (NullPointerException e) {
            throw new PersistenceHistoryException("Activity log failed to save due to null pointer: " + domain);
        }
    }
}
