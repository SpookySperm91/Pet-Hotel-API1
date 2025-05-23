package john.api1.application.adapters.controllers.admin;

import jakarta.validation.Valid;
import john.api1.application.async.AsyncNotificationService;
import john.api1.application.components.enums.NotificationType;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.request.commit.RequestCommittedPhotoDTO;
import john.api1.application.dto.mapper.request.commit.RequestCommittedServiceDTO;
import john.api1.application.dto.mapper.request.commit.RequestCommittedVideoDTO;
import john.api1.application.dto.request.NotificationRDTO;
import john.api1.application.dto.request.request.admin.RequestCompletePhotoRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteServiceRDTO;
import john.api1.application.dto.request.request.admin.RequestCompleteVideoRDTO;
import john.api1.application.ports.services.request.admin.ICommitRequestMedia;
import john.api1.application.ports.services.request.admin.ICommitRequestServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(AdminRequestCommitController.class);
    private final ICommitRequestMedia commitRequestMedia;
    private final ICommitRequestServices commitRequestServices;
    private final AsyncNotificationService notificationService;

    @Autowired
    public AdminRequestCommitController(ICommitRequestMedia commitRequestMedia,
                                        ICommitRequestServices commitRequestServices,
                                        AsyncNotificationService notificationService) {
        this.commitRequestMedia = commitRequestMedia;
        this.commitRequestServices = commitRequestServices;
        this.notificationService = notificationService;
    }


    @PostMapping("/media-photo")
    public ResponseEntity<DTOResponse<RequestCommittedPhotoDTO>> completePhotoRequest(
            @Valid @RequestBody RequestCompletePhotoRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            var commit = commitRequestMedia.commitPhotoRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            var data = commit.getData();
            try {
                var notification = new NotificationRDTO(
                        data.ownerId(),
                        null,
                        data.requestId(),
                        RequestType.PHOTO_REQUEST.getRequestType(),
                        NotificationType.PHOTO_REQUEST_COMPLETED.getNotificationType());
                notificationService.tryCreate(notification);
            } catch (DomainArgumentException | PersistenceException e) {
                log.warn("Failed to create complete photo request notification. Reason: {}", e.getMessage());
            }

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
    public ResponseEntity<DTOResponse<RequestCommittedVideoDTO>> completeVideoRequest(
            @Valid @RequestBody RequestCompleteVideoRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            var commit = commitRequestMedia.commitVideoRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            var data = commit.getData();
            try {
                var notification = new NotificationRDTO(
                        data.ownerId(),
                        null,
                        data.requestId(),
                        RequestType.VIDEO_REQUEST.getRequestType(),
                        NotificationType.VIDEO_REQUEST_COMPLETED.getNotificationType());
                notificationService.tryCreate(notification);
            } catch (DomainArgumentException | PersistenceException e) {
                log.warn("Failed to create complete video request notification. Reason: {}", e.getMessage());
            }

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
    public ResponseEntity<DTOResponse<RequestCommittedServiceDTO>> completeExtensionRequest(
            @Valid @RequestBody RequestCompleteServiceRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            System.out.println("request id: " + request.getRequestId());
            System.out.println("notes: " + request.getNotes());

            var commit = commitRequestServices.commitExtensionRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            var data = commit.getData();
            try {
                var notification = new NotificationRDTO(
                        data.ownerId(),
                        null,
                        data.requestId(),
                        RequestType.BOARDING_EXTENSION.getRequestType(),
                        NotificationType.EXTENSION_REQUEST_COMPLETED.getNotificationType());
                notificationService.tryCreate(notification);
            } catch (DomainArgumentException | PersistenceException e) {
                log.warn("Failed to create complete extension request notification. Reason: {}", e.getMessage());
            }

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
    public ResponseEntity<DTOResponse<RequestCommittedServiceDTO>> completeGroomingRequest(
            @Valid @RequestBody RequestCompleteServiceRDTO request,
            BindingResult result
    ) {
        // Check for errors
        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        try {
            var commit = commitRequestServices.commitGroomingRequest(request);
            if (!commit.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, commit.getMessage());

            var data = commit.getData();
            try {
                var notification = new NotificationRDTO(
                        data.ownerId(),
                        null,
                        data.requestId(),
                        RequestType.GROOMING_SERVICE.getRequestType(),
                        NotificationType.GROOMING_REQUEST_COMPLETED.getNotificationType());
                notificationService.tryCreate(notification);
            } catch (DomainArgumentException | PersistenceException e) {
                log.warn("Failed to create grooming request notification. Reason: {}", e.getMessage());
            }

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

