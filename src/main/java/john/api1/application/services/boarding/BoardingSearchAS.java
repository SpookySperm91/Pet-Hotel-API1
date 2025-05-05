package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.boarding.BoardingExtensionDS;
import john.api1.application.domain.cores.boarding.BoardingManagementDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.mapper.boarding.BoardingDTO;
import john.api1.application.ports.repositories.boarding.BoardingDurationCQRS;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
import john.api1.application.ports.repositories.request.IRequestCompletedSearchRepository;
import john.api1.application.ports.services.IBoardingAggregation;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.pet.IPetSearch;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardingSearchAS implements IBoardingSearch {
    private static final Logger logger = LoggerFactory.getLogger(BoardingSearchAS.class);
    private final IBoardingSearchRepository searchRepository;
    private final IPricingManagement pricingSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;
    private final IRequestCompletedSearchRepository requestSearch;
    private final IBoardingAggregation aggregation;

    @Autowired
    public BoardingSearchAS(IBoardingSearchRepository searchRepository,
                            IPricingManagement pricingSearch,
                            IPetOwnerSearch ownerSearch,
                            IPetSearch petSearch,
                            IRequestCompletedSearchRepository requestSearch,
                            IBoardingAggregation aggregation) {
        this.searchRepository = searchRepository;
        this.pricingSearch = pricingSearch;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
        this.requestSearch = requestSearch;
        this.aggregation = aggregation;
    }


    @Override
    public DomainResponse<Void> isBoardingActive(String boardingId) {
        try {
            validateId(boardingId, "boarding");

            var active = searchRepository.checkBoardingCurrentStatus(boardingId);
            if (active.isEmpty()) return DomainResponse.error("boarding cannot be found!");

            BoardingStatus status = active.get();
            if (BoardingStatus.RELEASED.equals(status))
                return DomainResponse.error("Boarding is already released and is inactive");

            return DomainResponse.success("Boarding is currently marked as " + status.getBoardingStatus().toLowerCase() + ".");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }

    @Override
    public DomainResponse<BoardingDomain> findBoardingById(String boardingId) {
        try {
            validateId(boardingId, "boarding");
            var boarding = searchRepository.searchById(boardingId);
            return boarding.map(domain -> DomainResponse.success(domain, "Boarding is successfully retrieved"))
                    .orElseGet(() -> DomainResponse.error("Boarding cannot be found!"));

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }

    // history
    @Override
    public DomainResponse<List<BoardingDTO>> findAllByOwnerId(String ownerId) {
        try {
            validateId(ownerId, "owner");
            var boarding = searchRepository.searchAllByOwnerId(ownerId);

            if (boarding.isEmpty()) {
                return DomainResponse.error("No boardings found for this owner.");
            }
            var dto = aggregation(boarding);

            return DomainResponse.success(dto, "Boardings were successfully retrieved.");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }

    @Override
    public DomainResponse<List<BoardingDomain>> allBoardingByStatus(BoardingStatus status) {
        try {
            var boarding = searchRepository.searchByStatus(status);

            if (boarding.isEmpty()) {
                return DomainResponse.error("No " + status.getBoardingStatus() + " boarding found.");
            }

            return DomainResponse.success(boarding, status.getBoardingStatus() + " boarding successfully retrieved.");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }

    @Override
    public DomainResponse<List<BoardingDTO>> allBoarding() {
        try {
            var boarding = searchRepository.searchAll();

            if (boarding.isEmpty())
                return DomainResponse.error("No boarding found.");

            var dto = aggregation(boarding);
            return DomainResponse.success(dto, " All boarding successfully retrieved.");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }


    @Override
    public DomainResponse<BoardingDTO> recentBoarding() {

        try {
            var boarding = searchRepository.searchRecent();

            if (boarding.isEmpty()) return DomainResponse.error("No boarding found.");

            var dto = aggregation(boarding.get());
            return dto.map(boardingDTO ->
                            DomainResponse.success(boardingDTO, " Recent boarding successfully retrieved."))
                    .orElseGet(() -> DomainResponse.error("Boarding data aggregation failed. Try again"));

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }


    @Override
    public BoardingDurationCQRS checkBoardingTime(String id) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid boarding id cannot be converted to ObjectId");

        var boardingTime = searchRepository.checkBoardingTime(id);
        if (boardingTime.isEmpty())
            throw new PersistenceException("Boarding time cannot be found.");

        return boardingTime.get();
    }


    private void validateId(String id, String type) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid " + type + " id cannot be converted to ObjectId");
    }


    private List<BoardingDTO> aggregation(List<BoardingDomain> boardings) {
        List<BoardingDTO> result = new ArrayList<>(boardings.size());

        for (BoardingDomain boarding : boardings) {
            try {
                var pricingResult = pricingSearch.getPricingDetails(boarding.getId());
                var pricing = pricingResult != null ? pricingResult.getData() : null;

                var owner = ownerSearch.getPetOwnerBoardingDetails(boarding.getOwnerId());
                var pet = petSearch.getPetBoardingDetails(boarding.getPetId());

                // Base end time before extensions
                Instant endTime = boarding.getBoardingEnd();
                long hours = BoardingManagementDS.calculateBoardingDurationHours(boarding.getBoardingStart(), endTime);
                long days = BoardingManagementDS.calculateBoardingDurationDays(boarding.getBoardingStart(), endTime);

                var extensions = requestSearch.getExtensionByCurrentBoarding(boarding.getId());
                if (extensions != null && !extensions.isEmpty()) {
                    endTime = BoardingExtensionDS.calculateFinalBoardingEnd(endTime, extensions);
                    hours = BoardingManagementDS.calculateBoardingDurationHours(boarding.getBoardingStart(), endTime);
                    days = BoardingManagementDS.calculateBoardingDurationDays(boarding.getBoardingStart(), endTime);
                }

                result.add(aggregation.boardingAggregation(boarding, pricing, owner, pet, days, hours, endTime));

            } catch (PersistenceException | IllegalArgumentException e) {
                logger.warn("Failed to aggregate boarding while searching. ID :{}. Error: {}", boarding.getId(), e.getMessage());
            }
        }

        return result;
    }


    private Optional<BoardingDTO> aggregation(BoardingDomain boarding) {
        try {
            var pricingResult = pricingSearch.getPricingDetails(boarding.getId());
            var pricing = pricingResult != null ? pricingResult.getData() : null;

            var owner = ownerSearch.getPetOwnerBoardingDetails(boarding.getOwnerId());
            var pet = petSearch.getPetBoardingDetails(boarding.getPetId());

            // Base end time before extensions
            Instant endTime = boarding.getBoardingEnd();
            long hours = BoardingManagementDS.calculateBoardingDurationHours(boarding.getBoardingStart(), endTime);
            long days = BoardingManagementDS.calculateBoardingDurationDays(boarding.getBoardingStart(), endTime);

            var extensions = requestSearch.getExtensionByCurrentBoarding(boarding.getId());
            if (extensions != null && !extensions.isEmpty()) {
                endTime = BoardingExtensionDS.calculateFinalBoardingEnd(endTime, extensions);
                hours = BoardingManagementDS.calculateBoardingDurationHours(boarding.getBoardingStart(), endTime);
                days = BoardingManagementDS.calculateBoardingDurationDays(boarding.getBoardingStart(), endTime);
            }

            return Optional.ofNullable(aggregation.boardingAggregation(boarding, pricing, owner, pet, days, hours, endTime));

        } catch (PersistenceException | IllegalArgumentException e) {
            logger.warn("Failed to aggregate boarding while searching. ID :{}. Error: {}", boarding.getId(), e.getMessage());
        }
        return Optional.empty();
    }


}
