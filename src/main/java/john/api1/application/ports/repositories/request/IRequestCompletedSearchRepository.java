package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.*;

import java.util.List;
import java.util.Optional;

public interface IRequestCompletedSearchRepository {
    List<ExtensionDomain> getExtensionByCurrentBoarding(String boardingId);

    // ID

    Optional<GroomingDomain> getGroomingByRequestId(String id);

    Optional<ExtensionDomain> getExtensionByRequestId(String id);
    Optional<PhotoRequestDomain> findPhotoRequestByRequestId(String id);
    Optional<VideoRequestDomain> findVideoRequestByRequestId(String id);



    // CQRS
    Optional<GroomingCQRS> getGroomingByRequestIdCqrs(String id);
    Optional<ExtensionCQRS> getExtensionByRequestIdCqrs(String id);

    // Recent
    Optional<PhotoRequestDomain> findRecentPhotoRequest();
    Optional<VideoRequestDomain> findRecentVideoRequest();



}
