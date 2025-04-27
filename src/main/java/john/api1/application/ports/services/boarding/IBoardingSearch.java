package john.api1.application.ports.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.ports.repositories.boarding.BoardingDurationCQRS;

import java.util.List;
import java.util.Optional;

public interface IBoardingSearch {
    DomainResponse<Void> isBoardingActive(String boardingId);
    DomainResponse<BoardingDomain> findBoardingById(String boardingId);

    // History
    DomainResponse<List<BoardingDomain>> findAllByOwnerId(String ownerId);

    // Preload
    DomainResponse<List<BoardingDomain>> allBoardingByStatus(BoardingStatus status);
    DomainResponse<List<BoardingDomain>> allBoarding();

    // Optional/Unsafe
    BoardingDurationCQRS checkBoardingTime(String id);


}
