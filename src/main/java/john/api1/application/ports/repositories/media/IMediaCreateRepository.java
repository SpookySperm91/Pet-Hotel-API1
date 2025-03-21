package john.api1.application.ports.repositories.media;

import john.api1.application.domain.models.MediaDomain;
import john.api1.application.ports.repositories.records.MediaPreview;

import java.util.Optional;

public interface IMediaCreateRepository {
    Optional<MediaDomain> save(MediaDomain domain);
}
