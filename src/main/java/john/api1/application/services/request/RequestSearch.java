package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.services.request.IRequestSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestSearch implements IRequestSearch {
    private final IRequestSearchRepository searchRepository;

    @Autowired
    public RequestSearch(IRequestSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public RequestDomain searchById(String requestId) {
        validateId(requestId);
        return searchRepository.findById(requestId)
                .orElseThrow(() -> new PersistenceException("Request cannot be found"));
    }

    @Override
    public List<RequestDomain> searchRequestByBoardingId(String boardingId) {
        validateId(boardingId);
        List<RequestDomain> requests = searchRepository.findByBoardingId(boardingId);
        return requests.isEmpty() ?
                List.of() :
                requests;
    }

    @Override
    public DomainResponse<RequestDomain> safeSearchById(String requestId) {
        try {
            validateId(requestId);
            var request = searchRepository.findById(requestId);
            return request.map(domain -> DomainResponse.success(domain, "Successfully retrieve request"))
                    .orElseGet(() -> DomainResponse.error("Request cannot be found"));
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something went wrong. Please try again later.");
        }
    }

    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
    }
}
