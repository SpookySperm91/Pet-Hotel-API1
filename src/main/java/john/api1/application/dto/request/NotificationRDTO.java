package john.api1.application.dto.request;

import jakarta.annotation.Nullable;
import john.api1.application.components.annotation.notification.ValidNotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRDTO {
    @NotBlank(message = "owner id cannot be empty")
    private String ownerId;
    @Nullable
    private String requestId;
    @NotBlank(message = "notification type cannot be empty")
    @ValidNotificationType
    private String notificationType;
}


