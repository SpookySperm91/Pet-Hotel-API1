package john.api1.application.services.notification;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.NotificationType;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.NotificationContext;
import john.api1.application.domain.cores.NotificationDescriptionDS;
import john.api1.application.domain.cores.boarding.BoardingExtensionDS;
import john.api1.application.domain.models.request.NotificationDomain;
import john.api1.application.dto.mapper.NotificationDTO;
import john.api1.application.dto.request.NotificationRDTO;
import john.api1.application.ports.repositories.notification.INotificationCreateRepository;
import john.api1.application.ports.repositories.notification.INotificationManageRepository;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.notification.INotificationCreate;
import john.api1.application.ports.services.notification.INotificationDelete;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearch;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationAS implements INotificationCreate, INotificationDelete {
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM d, yyyy").withZone(SYSTEM_ZONE);
    private final INotificationCreateRepository createRepository;
    private final INotificationManageRepository deleteRepository;
    private final IRequestSearch requestSearch;
    private final IBoardingSearch boardingSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;

    @Autowired
    public NotificationAS(INotificationCreateRepository createRepository,
                          INotificationManageRepository deleteRepository,
                          IRequestSearch requestSearch,
                          IBoardingSearch boardingSearch,
                          IPetOwnerSearch ownerSearch,
                          IPetSearch petSearch) {
        this.createRepository = createRepository;
        this.deleteRepository = deleteRepository;
        this.requestSearch = requestSearch;
        this.boardingSearch = boardingSearch;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
    }

    // Create
    // Check ids if null or not
    // If not: Check request status if null or not
    // Fetch the data (Grooming, Extension, Boarding)
    // Instantiates the builder for description generator
    // Generate description and create domain
    // Save to DB
    // Map to DTO to return
    @Override
    public DomainResponse<NotificationDTO> createNotification(NotificationRDTO notification) {
        try {
            validateId(notification.getOwnerId(), "owner");
            if (notification.getPetId() != null) validateId(notification.getPetId(), "pet");
            if (notification.getRequestId() != null) validateId(notification.getRequestId(), "request");

            RequestType requestType = null;
            if (notification.getRequestType() != null) {
                requestType = RequestType.fromString(notification.getRequestType());
            }

            NotificationType notificationType = NotificationType.fromString(notification.getNotificationType());

            // Search for related info (if needed)
            String ownerName = ownerSearch.getPetOwnerName(notification.getOwnerId());
            String petName = notification.getPetId() != null ? petSearch.getPetName(notification.getPetId()) : null;

            // Extension
            BoardingType boardingType = null;

            // Find extra context if needed (example: request type, boarding type, etc.)
            Instant checkoutTime = null;
            Instant newDuration = null;
            double charges = 0.0;

            if (notification.getRequestId() != null && requestType != null) {
                switch (requestType) {
                    case GROOMING_SERVICE -> {
                        var requestDetails = requestSearch.searchGroomingByRequestIdCqrs(notification.getRequestId());
                        charges = requestDetails.price();
                    }
                    case BOARDING_EXTENSION -> {
                        var requestDetails = requestSearch.searchExtensionByRequestIdCqrs(notification.getRequestId());
                        var boardingTime = boardingSearch.checkBoardingTime(requestDetails.boardingId());

                        boardingType = boardingTime.boardingType();
                        checkoutTime = boardingTime.checkOut();
                        newDuration = BoardingExtensionDS.calculateFinalBoardingEnd(checkoutTime, requestDetails);
                        charges = requestDetails.additionalPrice();
                    }
                    default -> {
                        charges = 0.0;
                    }
                }
            }

            // Create the NotificationContext (your domain object)
            var context = NotificationContext.builder()
                    .notificationType(notificationType)
                    .ownerName(ownerName)
                    .petName(petName)
                    .requestType(requestType)
                    .boardingType(boardingType)
                    .newDuration(newDuration)
                    .charges(charges)
                    .checkoutTime(checkoutTime)
                    .build();

            String description = NotificationDescriptionDS.description(context);

            // Create the NotificationDomain and pass it to repository
            NotificationDomain domain = NotificationDomain.create(notification.getRequestId(),
                    notification.getOwnerId(), description, notificationType);

            // Save the domain
            var saved = createRepository.save(domain); // Assuming save method works with NotificationDomain
            if (saved.isEmpty())
                throw new PersistenceException("Notification failed to saved to database after final step");
            domain = NotificationDomain.mapWithId(saved.get(), domain);

            // Map to DTO for response
            NotificationDTO response = NotificationDTO.map(domain);
            return DomainResponse.success(response);

        } catch (DomainArgumentException | PersistenceException e) {
            System.out.println(e.fillInStackTrace().getMessage());
            return DomainResponse.error(e.getMessage());
        }
    }

    // Delete
    @Override
    public DomainResponse<Void> deleteById(String id) {
        try {
            validateId(id, "notification");
            deleteRepository.deleteById(id);

            return DomainResponse.success("Successfully delete notification");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }


    @Override
    public DomainResponse<Void> deleteAllRead(String ownerId) {
        try {
            validateId(ownerId, "owner");
            var count = deleteRepository.deleteAllRead(ownerId);

            return DomainResponse.success("Successfully deleted " + count + " read notification(s).");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    @Override
    public DomainResponse<Void> deleteAllByDay(String ownerId, Instant day) {
        try {
            validateId(ownerId, "owner");
            var count = deleteRepository.deleteAllByDay(ownerId, day);

            String formattedDate = DATE_FORMAT.format(day);
            return DomainResponse.success("Successfully deleted " + count + " notification(s) from " + formattedDate);
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }


    private void validateId(String id, String type) {
        System.out.println(type  + "-id: " + id);
        if (!ObjectId.isValid(id))
            throw new PersistenceException("Invalid " + type + " id cannot be converted to ObjectId");
    }
}
