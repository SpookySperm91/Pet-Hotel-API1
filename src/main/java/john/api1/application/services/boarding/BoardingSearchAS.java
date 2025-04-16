package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardingSearchAS implements IBoardingSearch {
    private final IBoardingSearchRepository searchRepository;

    @Autowired
    public BoardingSearchAS(IBoardingSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
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
    public DomainResponse<List<BoardingDomain>> findAllByOwnerId(String ownerId) {
        try {
            validateId(ownerId, "owner");
            var boarding = searchRepository.searchAllByOwnerId(ownerId);

            if (boarding.isEmpty()) {
                return DomainResponse.error("No boardings found for this owner.");
            }

            return DomainResponse.success(boarding, "Boardings were successfully retrieved.");

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
    public DomainResponse<List<BoardingDomain>> allBoarding() {
        try {
            var boarding = searchRepository.searchAll();

            if (boarding.isEmpty()) {
                return DomainResponse.error("No boarding found.");
            }

            return DomainResponse.success(boarding, " All boarding successfully retrieved.");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("Something wrong with the database, try again later.");
        }
    }

    private void validateId(String id, String type) {
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid " + type + " id cannot be converted to ObjectId");
    }

}
