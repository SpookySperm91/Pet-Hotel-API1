package john.api1.application.ports.repositories.boarding;

import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.domain.models.boarding.BoardingDomain;

import java.util.List;
import java.util.Optional;

public interface IBoardingSearchRepository {
    Optional<BoardingDomain> searchById(String id);

    List<BoardingDomain> searchAllByOwnerId(String ownerId);

    List<BoardingDomain> searchAllByPetId(String petId);

    List<BoardingDomain> searchActiveBoardingsByOwnerId(String ownerId);

    List<BoardingDomain> searchAll();

    Optional<BoardingDomain> searchRecent();

    // Dynamic
    List<BoardingDomain> searchByStatus(BoardingStatus status);

    // Status
    Optional<BoardingStatus> checkBoardingCurrentStatus(String id);

    // Time
    Optional<BoardingDurationCQRS> checkBoardingTime(String id);

    List<BoardingDomain> searchAllCompletedByPetId(String petId);

}
