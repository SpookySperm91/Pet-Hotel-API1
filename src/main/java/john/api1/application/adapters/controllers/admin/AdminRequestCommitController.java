package john.api1.application.adapters.controllers.admin;

import jakarta.validation.Valid;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.request.commit.RequestCompletedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCompletedServiceDTO;
import john.api1.application.dto.mapper.request.commit.RequestCompletedVideoDTO;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;
import john.api1.application.ports.services.request.admin.ICommitRequestMedia;
import john.api1.application.ports.services.request.admin.ICommitRequestServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/manage/request/commit")
public class AdminRequestCommitController {
    private final ICommitRequestMedia commitRequestMedia;
    private final ICommitRequestServices commitRequestServices;

    @Autowired
    public AdminRequestCommitController(ICommitRequestMedia commitRequestMedia,
                                        ICommitRequestServices commitRequestServices) {
        this.commitRequestMedia = commitRequestMedia;
        this.commitRequestServices = commitRequestServices;
    }


    @PostMapping("/media-photo")
    public ResponseEntity<DTOResponse<RequestCompletedPhotoDTO>> completePhotoRequest(
            @Valid @RequestBody RequestCompletePhotoRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            //////////////////////////
            // Session and magic shits
            //////////////////////////

            var commit = commitRequestMedia.commitPhotoRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(DTOResponse.of(
                            HttpStatus.OK.value(),
                            commit.getData(),
                            commit.getMessage()));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong. Try again!");
        }
    }


    @PostMapping("/media-video")
    public ResponseEntity<DTOResponse<RequestCompletedVideoDTO>> completeVideoRequest(
            @Valid @RequestBody RequestCompleteVideoRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            //////////////////////////
            // Session and magic shits
            //////////////////////////

            var commit = commitRequestMedia.commitVideoRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(DTOResponse.of(
                            HttpStatus.OK.value(),
                            commit.getData(),
                            commit.getMessage()));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong. Try again!");
        }
    }


    @PostMapping("/service-extension")
    public ResponseEntity<DTOResponse<RequestCompletedServiceDTO>> completeExtensionRequest(
            @Valid @RequestBody RequestCompleteServiceRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            //////////////////////////
            // Session and magic shits
            //////////////////////////

            var commit = commitRequestServices.commitExtensionRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(DTOResponse.of(
                            HttpStatus.OK.value(),
                            commit.getData(),
                            commit.getMessage()));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong. Try again!");
        }
    }

    @PostMapping("/service-grooming")
    public ResponseEntity<DTOResponse<RequestCompletedServiceDTO>> completeGroomingRequest(
            @Valid @RequestBody RequestCompleteServiceRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            //////////////////////////
            // Session and magic shits
            //////////////////////////

            var commit = commitRequestServices.commitGroomingRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            return ResponseEntity.status(HttpStatus.OK)
                    .body(DTOResponse.of(
                            HttpStatus.OK.value(),
                            commit.getData(),
                            commit.getMessage()));
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong. Try again!");
        }
    }


    private String checkValidation(BindingResult result) {
        if (result.hasErrors()) {
            return result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        }
        return null;
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}

