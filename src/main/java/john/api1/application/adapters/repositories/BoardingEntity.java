package john.api1.application.adapters.repositories;

import john.api1.application.domain.models.boarding.BoardingDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "${db.collection.boarding}")
public class BoardingEntity {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId petId;
    @Indexed
    private ObjectId ownerId;
    private String boardingCategory; // DAYCARE, LONG_STAY
    private Instant boardingStart;
    private Instant boardingEnd;
    private String boardingStatus;  // BOARDING, DONE_BOARDING, OVERDUE, RELEASED
    private String paymentStatus; // PAID, NOT_PAID, PENDING
    private String notes;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    private boolean active;

    public static BoardingEntity createWithDomain(BoardingDomain domain) {
        return new BoardingEntity(
                null,
                new ObjectId(domain.getPetId()),
                new ObjectId(domain.getOwnerId()),
                domain.getBoardingType().getBoardingType(),
                domain.getBoardingStart(),
                domain.getBoardingEnd(),
                domain.getBoardingStatus().getBoardingStatus(),
                domain.getPaymentStatus().getPaymentStatus(),
                domain.getNotes(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.isActive()
        );
    }

    public static BoardingEntity map(BoardingDomain domain) {
        return new BoardingEntity(
                new ObjectId(domain.getId()),
                new ObjectId(domain.getPetId()),
                new ObjectId(domain.getOwnerId()),
                domain.getBoardingType().getBoardingType(),
                domain.getBoardingStart(),
                domain.getBoardingEnd(),
                domain.getBoardingStatus().getBoardingStatus(),
                domain.getPaymentStatus().getPaymentStatus(),
                domain.getNotes(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.isActive()
        );
    }
}
