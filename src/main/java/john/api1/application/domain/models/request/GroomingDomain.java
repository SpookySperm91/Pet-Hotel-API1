package john.api1.application.domain.models.request;

import john.api1.application.components.enums.GroomingType;
import john.api1.application.components.enums.PetPrices;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class GroomingDomain {
    private final String id;
    private final String requestId;
    private final String boardingId;
    private GroomingType groomingType;
    private double groomingPrice;
    @Setter
    private String description;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean approved = false;

    public GroomingDomain(String requestId, String boardingId, String description) {
        if (!ObjectId.isValid(requestId) || !ObjectId.isValid(boardingId))
            throw new DomainArgumentException("Id is invalid format");

        this.id = null;
        this.requestId = requestId;
        this.boardingId = boardingId;
        this.description = description;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.approved = false;
    }

    public GroomingDomain copy(){
        return this.toBuilder().build();
    }


    public GroomingDomain mapWithId(String id) {
        return new GroomingDomain(id, this.requestId, this.boardingId, this.groomingType, this.groomingPrice, this.description, this.createdAt, this.updatedAt, this.approved);
    }


    public void setGroomingAndPrice(GroomingType groomingType, PetPrices petPrices) {
        switch (groomingType) {
            case BASIC_WASH -> this.groomingPrice = petPrices.getBasicGroomingPrice();
            case FULL_GROOMING -> this.groomingPrice = petPrices.getFullGroomingPrice();
            default -> throw new DomainArgumentException("Invalid grooming type: " + groomingType);
        }
        this.groomingType = groomingType;
        this.updatedAt = Instant.now();
    }

    public void markAsApproved() {
        if (!approved) {
            this.approved = true;
            this.updatedAt = Instant.now();
            return;
        }
        throw new DomainArgumentException("Grooming request is already approved");
    }
}
