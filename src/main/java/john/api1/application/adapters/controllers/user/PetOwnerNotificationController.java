package john.api1.application.adapters.controllers.user;

import john.api1.application.dto.mapper.NotificationDTO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/pet-owner/notifications")
public class PetOwnerNotificationController {
    private static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Subscribes after pet-owner logging in
    @GetMapping(value = "/subscribe/{ownerId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String ownerId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitters.put(ownerId, emitter);

        emitter.onCompletion(() -> emitters.remove(ownerId));
        emitter.onTimeout(() -> emitters.remove(ownerId));
        emitter.onError((e) -> emitters.remove(ownerId));

        return emitter;
    }

    @PostMapping("/send/{ownerId}")
    public void sendNotification(@PathVariable String ownerId,
                                 @RequestBody NotificationDTO notificationDTO) throws IOException {
        SseEmitter emitter = emitters.get(ownerId);

        if (emitter != null) {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notificationDTO));
        }
    }


    public static void sendToOwner(String ownerId,
                                   String notificationMessage) {
        SseEmitter emitter = emitters.get(ownerId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notificationMessage));  // Send the structured data, could be NotificationDTO
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(ownerId);
            }
        }
    }

}
