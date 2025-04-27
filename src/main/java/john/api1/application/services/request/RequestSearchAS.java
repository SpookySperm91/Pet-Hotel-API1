package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.request.*;
import john.api1.application.ports.services.request.IRequestSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestSearchAS implements IRequestSearch {
    private final IRequestSearchRepository searchRepository;
    private final IRequestCompletedSearchRepository searchCompletedRepository;

    @Autowired
    public RequestSearchAS(IRequestSearchRepository searchRepository,
                           IRequestCompletedSearchRepository searchCompletedRepository) {
        this.searchRepository = searchRepository;
        this.searchCompletedRepository = searchCompletedRepository;
    }

    @Override
    public RequestDomain searchByRequestId(String requestId) {
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

    // Specifics
    @Override
    public GroomingDomain searchGroomingByRequestId(String id) {
        validateId(id);
        return searchCompletedRepository.getGroomingByRequestId(id)
                .orElseThrow(() -> new PersistenceException("Grooming request cannot be found"));
    }

    @Override
    public ExtensionDomain searchExtensionByRequestId(String id) {
        validateId(id);
        return searchCompletedRepository.getExtensionByRequestId(id)
                .orElseThrow(() -> new PersistenceException("Extension request cannot be found"));
    }

    // CQRS
    @Override
    public GroomingCQRS searchGroomingByRequestIdCqrs(String id) {
        validateId(id);
        return searchCompletedRepository.getGroomingByRequestIdCqrs(id)
                .orElseThrow(() -> new PersistenceException("Grooming request cannot be found"));
    }

    @Override
    public ExtensionCQRS searchExtensionByRequestIdCqrs(String id) {
        validateId(id);
        return searchCompletedRepository.getExtensionByRequestIdCqrs(id)
                .orElseThrow(() -> new PersistenceException("Extension request cannot be found"));
    }

    @Override
    public Double searchGroomingPriceByRequestId(String id) {
        validateId(id);
        var price = searchCompletedRepository.getGroomingPriceByRequestId(id);
        if (price.isEmpty()) throw new PersistenceException("Grooming price cannot be found");
        return price.get();
    }

    @Override
    public Double searchExtensionPriceByRequestId(String id) {
        validateId(id);
        var price = searchCompletedRepository.getExtensionPriceByRequestId(id);
        if (price.isEmpty()) throw new PersistenceException("Extension price cannot be found");
        return price.get();
    }


    @Override
    public Optional<RequestCQRS> searchRecentMediaRequest() {
        return searchRepository.findRecentMediaRequest();
    }


    private void validateId(String id) {
        if (!ObjectId.isValid(id)) throw new PersistenceException("Invalid id cannot be converted to ObjectId");
    }
}
