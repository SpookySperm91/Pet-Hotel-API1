package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.request.RequestStatusUpdateDTO;
import john.api1.application.dto.request.request.admin.RejectRequestRDTO;
import john.api1.application.ports.services.request.IRequestStatusManagement;
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
    private final IRequestStatusManagement statusService;

    @Autowired
    public AdminRequestStatusController(IRequestStatusManagement statusService) {
        this.statusService = statusService;
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

        // Session magics (later)
        /////////////////////////
        /////////////////////////

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

        // Session magics (later)
        /////////////////////////
        /////////////////////////

        var approved = statusService.rejectRequest(request.getRequestId(), request.getMessage());
        if (!approved.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, approved.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        approved.getData(),
                        approved.getMessage()));
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

        // Session magics (later)
        /////////////////////////
        /////////////////////////

        var approved = statusService.revertToPending(requestId);
        if (!approved.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, approved.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        approved.getData(),
                        approved.getMessage()));
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

        // Session magics (later)
        /////////////////////////
        /////////////////////////

        var approved = statusService.undoReject(requestId);
        if (!approved.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, approved.getMessage());

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
