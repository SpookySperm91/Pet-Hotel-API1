package john.api1.application.services.boarding;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.request.BoardingStatusRDTO;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardingSearchAS implements IBoardingSearch {
    private final IBoardingSearchRepository searchRepository;

    public BoardingSearchAS(IBoardingSearchRepository searchRepository){
        this.searchRepository = searchRepository;
    }

    public DomainResponse<Void> isBoardingActive(String boardingId) {
        return DomainResponse.success();
    }

    public DomainResponse<BoardingStatusRDTO> findBoardingById(String boardingId) {
        return DomainResponse.success();
    }

    // history
    public DomainResponse<List<BoardingStatusRDTO>> findAllByOwnerID(String ownerId) {
        return DomainResponse.success();
    }

    public DomainResponse<List<BoardingDomain>> allBoardingByStatus(BoardingStatus status) {
        return DomainResponse.success();
    }

    public DomainResponse<List<BoardingDomain>> allBoarding() {
        return DomainResponse.success();
    }

}
