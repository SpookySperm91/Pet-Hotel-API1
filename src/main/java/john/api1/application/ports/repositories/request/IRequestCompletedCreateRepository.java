package john.api1.application.ports.repositories.request;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.PhotoRequestDomain;
import john.api1.application.domain.models.request.VideoRequestDomain;

import java.util.Optional;

public interface IRequestCompletedCreateRepository {
    Optional<String> createPhotoRequest(PhotoRequestDomain extension);
    Optional<String> createVideoRequest(VideoRequestDomain extension);
}
