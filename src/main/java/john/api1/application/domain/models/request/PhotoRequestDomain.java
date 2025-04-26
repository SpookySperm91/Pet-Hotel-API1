package john.api1.application.domain.models.request;

import john.api1.application.adapters.repositories.PhotoRequestEntity;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public record PhotoRequestDomain(
        String id,
        String requestId,
        String ownerId,
        List<MediaFile> photo,
        Instant uploadedAt) {

    public static PhotoRequestDomain create(String requestId, String ownerId, List<MediaFile> photosName) {
        return new PhotoRequestDomain(null, requestId, ownerId, photosName, Instant.now());
    }

    public PhotoRequestDomain mapWithId(String id) {
        return new PhotoRequestDomain(id, requestId, ownerId, photo, Instant.now());
    }

    public record MediaFile(
            String id, String fileName
    ) {

        public static List<MediaFile> map(List<PhotoRequestEntity.PhotoFileListEnt> photoList) {
            if (photoList == null) {
                return Collections.emptyList();
            }

            return photoList.stream()
                    .map(p -> new MediaFile(
                            p.mediaId().toHexString(),
                            p.fileName()
                    ))
                    .collect(Collectors.toList());
        }
    }


}
