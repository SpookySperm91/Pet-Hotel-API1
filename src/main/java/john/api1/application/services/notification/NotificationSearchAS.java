package john.api1.application.services.notification;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.NotificationDTO;
import john.api1.application.ports.repositories.notification.INotificationManageRepository;
import john.api1.application.ports.services.notification.INotificationSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationSearchAS implements INotificationSearch {
    private final INotificationManageRepository notificationSearch;

    @Autowired
    public NotificationSearchAS(INotificationManageRepository notificationSearch) {
        this.notificationSearch = notificationSearch;
    }

    public DomainResponse<NotificationDTO> searchById(String id) {
        if (!ObjectId.isValid(id)) return DomainResponse.error("Invalid id format");

        var search = notificationSearch.searchById(id);
        if (search.isEmpty()) return DomainResponse.error("Notification cannot be found!");

        var dto = NotificationDTO.map(search.get());


        return DomainResponse.success(dto, "Successfully retrieved notification");

    }

    public DomainResponse<NotificationDTO> searchOwnerRecent(String ownerId) {
        if (!ObjectId.isValid(ownerId)) return DomainResponse.error("Invalid owner id format");

        var search = notificationSearch.searchRecentByOwner(ownerId);
        if (search.isEmpty()) return DomainResponse.error("Recent notification cannot be found!");

        var dto = NotificationDTO.map(search.get());

        return DomainResponse.success(dto, "Successfully retrieved recent notification");
    }

    public DomainResponse<List<NotificationDTO>> searchAllByOwner(String ownerId) {
        if (!ObjectId.isValid(ownerId)) return DomainResponse.error("Invalid owner id format");


        var search = notificationSearch.searchAllByOwner(ownerId);
        if (search.isEmpty()) return DomainResponse.error("Recent notification cannot be found!");

        var dto = NotificationDTO.map(search);

        return DomainResponse.success(dto, "Successfully retrieved recent notification");
    }
}
