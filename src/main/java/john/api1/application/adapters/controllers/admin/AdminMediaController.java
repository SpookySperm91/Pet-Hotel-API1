package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;
import john.api1.application.services.media.MediaSearchAS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/admin/media")
public class AdminMediaController {
    private final MediaSearchAS mediaSearch;

    @Autowired
    public AdminMediaController(MediaSearchAS mediaSearch) {
        this.mediaSearch = mediaSearch;
    }

    @GetMapping("/search/pets/{id}/profile-photo")
    public ResponseEntity<DTOResponse<MediaIdUrlExpire>> searchPetProfile(
            @PathVariable @NotBlank String id
    ) {
        var media = mediaSearch.findProfilePicByOwnerId(id);
        return media.map(mediaIdUrlExpire -> ResponseEntity.status(HttpStatus.OK)
                        .body(DTOResponse.of(
                                HttpStatus.OK.value(),
                                mediaIdUrlExpire)))
                .orElseGet(() -> buildErrorResponse(HttpStatus.BAD_REQUEST, "Photo cannot be founded"));

    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
