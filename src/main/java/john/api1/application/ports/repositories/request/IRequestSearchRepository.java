package john.api1.application.ports.repositories.request;

import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.domain.models.request.RequestDomain;

import java.util.List;
import java.util.Optional;

public interface IRequestSearchRepository {
    Optional<RequestDomain> findById(String id);

    List<RequestDomain> findByBoardingId(String boardingId);

    List<RequestDomain> findAllByStatus(RequestStatus status);

    List<RequestDomain> findAll();

    // active, inactive
    List<RequestDomain> findAllActive();

    List<RequestDomain> findAllInactive();

    // pet history
    List<RequestDomain> findAllByPetId(String petId);

    // CQRS
    Optional<RequestCQRS> findRecentMediaRequest();
}
