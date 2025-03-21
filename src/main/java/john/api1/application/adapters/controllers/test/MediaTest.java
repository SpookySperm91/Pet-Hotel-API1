package john.api1.application.adapters.controllers.test;

import john.api1.application.components.enums.BucketType;
import john.api1.application.dto.DTOResponse;
import john.api1.application.ports.repositories.records.PreSignedUrlResponse;
import john.api1.application.services.media.MediaManagementAS;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/media/test/")
public class MediaTest {
    private final MediaManagementAS mediaManagement;

    public MediaTest(MediaManagementAS mediaManagement) {
        this.mediaManagement = mediaManagement;
    }


    @PutMapping("create")
    public ResponseEntity<DTOResponse<PreSignedUrlResponse>> createUploadLink() {
        String idTestValue = "67cd5d7ae0964d37102fa05a";
        String fileTestValue = "example-file";
        var response = mediaManagement.generateMediaFile(idTestValue, fileTestValue, BucketType.REQUEST_VIDEO);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DTOResponse.of(HttpStatus.CREATED.value(), response.getData()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DTOResponse.message(HttpStatus.BAD_REQUEST.value(), response.getMessage()));
    }

}
