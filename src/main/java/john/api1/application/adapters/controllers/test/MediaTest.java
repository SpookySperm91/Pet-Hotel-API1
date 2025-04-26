package john.api1.application.adapters.controllers.test;

import john.api1.application.components.enums.BucketType;
import john.api1.application.domain.models.MediaDomain;
import john.api1.application.dto.DTOResponse;
import john.api1.application.ports.repositories.wrapper.MediaPreview;
import john.api1.application.ports.repositories.wrapper.PreSignedUrlResponse;
import john.api1.application.ports.services.media.IMediaSearch;
import john.api1.application.services.media.MediaManagementAS;
import john.api1.application.services.media.MediaSearchAS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1/media/test/")
public class MediaTest {
    private final MediaManagementAS mediaManagement;
    private final IMediaSearch mediaSearch;

    @Autowired
    public MediaTest(MediaManagementAS mediaManagement, IMediaSearch mediaSearch) {
        this.mediaManagement = mediaManagement;
        this.mediaSearch = mediaSearch;
    }


    @PutMapping("create")
    public ResponseEntity<DTOResponse<PreSignedUrlResponse>> createUploadLink() {
        String idTestValue = "67cd5d7ae0964d37102fa05a";
        String fileTestValue = "example-file";
        BucketType fileType = BucketType.PROFILE_PHOTO;
        var response = mediaManagement.generateMediaFile(idTestValue, fileTestValue, fileType);

        if (response.isSuccess()) {
            var media = MediaDomain.create(
                    idTestValue,
                    null,
                    fileTestValue,
                    fileType,
                    null,
                    response.getData().expiresAt()
            );

            mediaManagement.saveMediaFile(media);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DTOResponse.of(HttpStatus.CREATED.value(), response.getData()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DTOResponse.message(HttpStatus.BAD_REQUEST.value(), response.getMessage()));
    }

    @GetMapping("read/{id}")
    public ResponseEntity<DTOResponse<List<MediaPreview>>> readMedia(@PathVariable String id) {
        var response = mediaSearch.findByOwnerId(id);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DTOResponse.of(HttpStatus.CREATED.value(), response.getData()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DTOResponse.message(HttpStatus.BAD_REQUEST.value(), response.getMessage()));
    }

}
