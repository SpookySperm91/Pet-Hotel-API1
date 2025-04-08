package john.api1.application.domain.models.request;

import john.api1.application.components.enums.PetPrices;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

// THIS DOMAIN WILL GENERATE AFTER REQUEST APPROVED AND COMMITTED
// MARK AS FINAL
@AllArgsConstructor
@Getter
public class ExtensionDomain {
    private final String id;
    private final String requestId;
    private final String boardingId;
    private double additionalPrice;
    private long extendedHours;
    @Setter
    private String description;
    private boolean paid = false;
    private final Instant createdAt;
    private Instant updatedAt;

    public ExtensionDomain(String id, String requestId, String boardingId, String description) {
        if (!ObjectId.isValid(id) || !ObjectId.isValid(requestId) || !ObjectId.isValid(boardingId))
            throw new DomainArgumentException("Id is invalid format");

        this.id = id;
        this.requestId = requestId;
        this.boardingId = boardingId;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void setAdditionalPrice(PetPrices petPrices, long extendedHours) {
        if (extendedHours <= 0) {
            throw new IllegalArgumentException("Extended hours must be greater than zero.");
        }

        this.additionalPrice = extendedHours * petPrices.getBoardingPrice();
        this.extendedHours = extendedHours;
    }

    public void markAsPaid() {
        if (!paid) {
            this.paid = true;
            this.updatedAt = Instant.now();
            return;
        }
        throw new DomainArgumentException("Boarding already paid");
    }
}
