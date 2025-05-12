package john.api1.application.adapters.controllers.user;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.NotificationDTO;
import john.api1.application.ports.services.notification.INotificationSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pet-owner/notifications")
public class PetOwnerNotificationController {
    private final INotificationSearch notificationSearch;

    @Autowired
    public PetOwnerNotificationController(INotificationSearch notificationSearch) {
        this.notificationSearch = notificationSearch;
    }


    // Subscribes after pet-owner logging in
    @GetMapping("/{ownerId}/all")
    public ResponseEntity<DTOResponse<List<NotificationDTO>>> getNotifications(@PathVariable String ownerId) {
        var notification = notificationSearch.searchAllByOwner(ownerId);
        if (!notification.isSuccess())
            return buildErrorResponse(HttpStatus.OK, "No current notification for owner id: " + ownerId);

        return ResponseEntity.status(HttpStatus.OK).body(
                DTOResponse.of(
                        HttpStatus.OK.value(),
                        notification.getData(),
                        "Notification successfully fetched")
        );
    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
