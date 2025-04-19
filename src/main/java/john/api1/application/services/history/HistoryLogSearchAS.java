package john.api1.application.services.history;

import john.api1.application.components.enums.ActivityLogType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.ActivityLogDS;
import john.api1.application.domain.cores.ActivityLogDataContext;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.mapper.history.ActivityLogDTO;
import john.api1.application.ports.repositories.history.IHistoryLogSearchRepository;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.history.IHistoryLogSearch;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistoryLogSearchAS implements IHistoryLogSearch {
    private static final Logger logger = LoggerFactory.getLogger(HistoryLogSearchAS.class);

    private final IHistoryLogSearchRepository searchRepository;
    private final IBoardingSearch boardingSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;
    private final IRequestSearch requestSearch;
    private final IPricingManagement pricingSearch;


    @Autowired
    public HistoryLogSearchAS(IHistoryLogSearchRepository searchRepository,
                              IBoardingSearch boardingSearch,
                              IPetOwnerSearch ownerSearch,
                              IPetSearch petSearch,
                              IRequestSearch requestSearch,
                              IPricingManagement pricingSearch) {
        this.searchRepository = searchRepository;
        this.boardingSearch = boardingSearch;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
        this.requestSearch = requestSearch;
        this.pricingSearch = pricingSearch;
    }

    public Optional<ActivityLogDTO> getRecentLog() {
        try {
            var activity = searchRepository.searchRecently();
            if (activity.isEmpty()) throw new PersistenceException("No recent activity log found");

            ActivityLogDomain domain = activity.get();
            ActivityLogDataContext context = buildDataContext(domain);

            ActivityLogDTO dto = transformLog(domain, context);

            return Optional.ofNullable(dto);
        } catch (PersistenceException e) {
            throw e;
        }
    }

    @Override
    public List<ActivityLogDTO> getAll() {
        try {
            List<ActivityLogDomain> activities = searchRepository.searchAll();
            if (activities.isEmpty()) {
                throw new PersistenceException("No activity logs found");
            }

            List<ActivityLogDTO> dtoList = activities.stream()
                    .map(domain -> {
                        ActivityLogDataContext context = buildDataContext(domain);
                        return transformLog(domain, context);
                    })
                    .collect(Collectors.toList());

            return dtoList;
        } catch (PersistenceException e) {
            logger.error("Error occurred while fetching activity logs: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<ActivityLogDTO> searchById(String id) {

        try {
            Optional<ActivityLogDomain> activity = searchRepository.searchById(id);

            if (activity.isEmpty()) {
                throw new PersistenceException("Activity log not found with ID: " + id);
            }

            ActivityLogDomain domain = activity.get();
            ActivityLogDataContext context = buildDataContext(domain);
            return Optional.ofNullable(transformLog(domain, context));
        } catch (PersistenceException e) {
            logger.error("Error occurred while fetching activity logs: {}", e.getMessage());
            throw e;
        }
    }

    // LOW PRIORITY ///////////////
    public Optional<List<ActivityLogDTO>> getByDate(Instant time) {
        return Optional.empty();
    }

    public Optional<List<ActivityLogDTO>> getByBetweenDate(Instant start, Instant end) {
        return Optional.empty();
    }
////////////////////////////////

    @Override
    public List<ActivityLogDTO> searchByActivityType(ActivityLogType type) {
        List<ActivityLogDomain> activities = searchRepository.searchByActivityType(type);
        if (activities.isEmpty()) {
            return List.of(); // Empty list if no activities of the given type
        }

        return activities.stream()
                .map(domain -> {
                    ActivityLogDataContext context = buildDataContext(domain);
                    return transformLog(domain, context);
                })
                .collect(Collectors.toList());
    }

    private ActivityLogDataContext buildDataContext(ActivityLogDomain domain) {
        switch (domain.getActivityType()) {
            case BOARDING_MANAGEMENT:
                // Fetch boarding details
                var boardingResult = boardingSearch.findBoardingById(domain.getTypeId());
                if (boardingResult.isSuccess()) {
                    BoardingDomain boarding = boardingResult.getData();

                    var pricing = pricingSearch.getBoardingPricingCqrs(boarding.getId());
                    var pet = petSearch.getPetBoardingDetails(boarding.getPetId());

                    var owner = ownerSearch.getPetOwnerBoardingDetails(boarding.getOwnerId());
                    return new ActivityLogDataContext.Builder()
                            .boarding(boarding)
                            .pricing(pricing.get())
                            .pet(pet)
                            .owner(owner) // Add owner details
                            .build();
                }
                break;

            case PET_OWNER_MANAGEMENT:
                // Fetch boarding first to get the ownerId
                var boardingForOwner = boardingSearch.findBoardingById(domain.getTypeId());
                if (boardingForOwner.isSuccess()) {
                    BoardingDomain boarding = boardingForOwner.getData();
                    var owner = ownerSearch.getPetOwnerBoardingDetails(boarding.getOwnerId());
                    return new ActivityLogDataContext.Builder()
                            .owner(owner)
                            .build();
                }
                break;

            case PET_MANAGEMENT:
                // Fetch boarding first to get the petId
                var boardingForPet = boardingSearch.findBoardingById(domain.getTypeId());
                if (boardingForPet.isSuccess()) {
                    BoardingDomain boarding = boardingForPet.getData();
                    var pet = petSearch.getPetBoardingDetails(boarding.getPetId());
                    return new ActivityLogDataContext.Builder()
                            .pet(pet)
                            .build();
                }
                break;

            case REQUEST_MANAGEMENT:
                // Fetch request details for request management activity
                var request = requestSearch.searchByRequestId(domain.getTypeId());
                if (request != null) {
                    return new ActivityLogDataContext.Builder()
                            .request(request)
                            .build();
                }
                break;

            default:
                break;
        }

        return null; // Return null if no context could be built
    }

    private ActivityLogDTO transformLog(ActivityLogDomain domain, ActivityLogDataContext context) {
        switch (domain.getActivityType()) {
            case BOARDING_MANAGEMENT:
                return ActivityLogDS.transformBoarding(domain, context);
            case PET_OWNER_MANAGEMENT:
                return ActivityLogDS.transformRegisterOwner(domain, context);
            case PET_MANAGEMENT:
                return ActivityLogDS.transformRegisterPet(domain, context);
            case REQUEST_MANAGEMENT:
                switch (domain.getRequestType()) {
                    case PHOTO_REQUEST:
                    case VIDEO_REQUEST:
                        return ActivityLogDS.transformRequestMedia(domain, context);
                    case BOARDING_EXTENSION:
                        return ActivityLogDS.transformRequestExtension(domain, context);
                    case GROOMING_SERVICE:
                        return ActivityLogDS.transformRequestGrooming(domain, context);
                    case null:
                        break;
                    default:
                        return null;
                }
            default:
                return null;
        }
    }


}
