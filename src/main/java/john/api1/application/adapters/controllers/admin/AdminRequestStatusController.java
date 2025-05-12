package john.api1.application.adapters.controllers.admin;

import john.api1.application.async.AsyncNotificationService;
import john.api1.application.components.enums.NotificationType;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.request.RequestStatusUpdateDTO;
import john.api1.application.dto.request.NotificationRDTO;
import john.api1.application.dto.request.request.admin.RejectRequestRDTO;
import john.api1.application.ports.services.request.IRequestStatusManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/manage/request/status")
public class AdminRequestStatusController {
    private static final Logger log = LoggerFactory.getLogger(AdminRequestStatusController.class);
    private final IRequestStatusManagement statusService;
    private final AsyncNotificationService notificationService;

    @Autowired
    public AdminRequestStatusController(IRequestStatusManagement statusService,
                                        AsyncNotificationService notificationService) {
        this.statusService = statusService;
        this.notificationService = notificationService;
    }

    // APPROVE REQUEST
    // Check request id if valid
    // Call service to approve request
    // Return DTO response
    @PutMapping("{requestId}/approve")
    public ResponseEntity<DTOResponse<RequestStatusUpdateDTO>> approveRequest(@PathVariable String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "request id cannot be null, empty, or blank!");
        }

        var approved = statusService.approvedRequest(requestId);
        if (!approved.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, approved.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        approved.getData(),
                        approved.getMessage()));
    }

    // REJECT REQUEST
    // Check request id if valid
    // Call service to reject request
    // Return DTO response
    @PutMapping("/reject")
    public ResponseEntity<DTOResponse<RequestStatusUpdateDTO>> rejectRequest(
            @RequestBody RejectRequestRDTO request,
            BindingResult result) {

        var error = checkValidation(result);
        if (error != null) return buildErrorResponse(HttpStatus.BAD_REQUEST, error);

        var reject = statusService.rejectRequest(request.getRequestId(), request.getMessage());
        if (!reject.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, reject.getMessage());

        var data = reject.getData();

        try {
            RequestType requestType = RequestType.fromString(data.requestType());
            RequestStatus requestStatus = RequestStatus.fromString(data.status());

            System.out.println(data.ownerId());

            var notification = new NotificationRDTO(
                    data.ownerId(),
                    null,
                    data.requestId(),
                    requestType.getRequestType(),
                    NotificationType.check(requestType, requestStatus).getNotificationType());
            notificationService.tryCreate(notification);
        } catch (DomainArgumentException | PersistenceException e) {
            log.warn("Failed to create reject notification. Reason: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        reject.getData(),
                        reject.getMessage()));
    }


    // REVERT APPROVED REQUEST TO PENDING
    // Check request id if valid
    // Call service to reject request
    // Return DTO response
    @PutMapping("{requestId}/revert-pending")
    public ResponseEntity<DTOResponse<RequestStatusUpdateDTO>> revertToPendingRequest(@PathVariable String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "request id cannot be null, empty, or blank!");
        }

        var pending = statusService.revertToPending(requestId);
        if (!pending.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, pending.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        pending.getData(),
                        pending.getMessage()));
    }

    // UNDO REJECTED REQUEST
    // Check request id if valid
    // Call service to reject request (Valid for 5 minutes)
    // Return DTO response
    @PutMapping("{requestId}/undo-rejected")
    public ResponseEntity<DTOResponse<RequestStatusUpdateDTO>> undoRejectedRequest(@PathVariable String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "request id cannot be null, empty, or blank!");
        }

        var approved = statusService.undoReject(requestId);
        if (!approved.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, approved.getMessage());

        var data = approved.getData();

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        approved.getData(),
                        approved.getMessage()));
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
