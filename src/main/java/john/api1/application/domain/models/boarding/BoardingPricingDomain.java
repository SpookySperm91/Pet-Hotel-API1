package john.api1.application.domain.models.boarding;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
public class BoardingPricingDomain {
    private final String id;
    private final String boardingId;
    private final double ratePerHour; // Standard rate per hour
    private final BoardingType boardingType; // "Daycare" or "Long-stay"
    private final long boardingDuration; // Hours if Daycare, Days if Long-stay
    private boolean isPrepaid; // True if the boarding is prepaid and pricing should be excluded
    private List<RequestBreakdown> requestBreakdown; // List of additional service requests
    private boolean isActive; // Marks if this pricing is still relevant
    private Instant deactivatedAt; // Timestamp when pricing became inactive


    // Create for new boarding (no request yet)
    public static BoardingPricingDomain createNew(String boardingId,
                                                  double ratePerHour,
                                                  BoardingType boardingType,
                                                  long boardingDuration,
                                                  boolean isPrepaid) {
        return new BoardingPricingDomain(null, boardingId, ratePerHour, boardingType, boardingDuration, isPrepaid, List.of(), true, null);
    }

    // Constructor for mapping DB records to domain objects
    public static BoardingPricingDomain mapping(String id,
                                                String boardingId,
                                                double ratePerHour,
                                                BoardingType boardingType,
                                                long boardingDuration,
                                                boolean isPrepaid,
                                                List<RequestBreakdown> requestBreakdown,
                                                boolean isActive,
                                                Instant deactivatedAt) {
        return new BoardingPricingDomain(id, boardingId, ratePerHour, boardingType, boardingDuration, isPrepaid,
                requestBreakdown != null ? requestBreakdown : List.of(),
                isActive, deactivatedAt);
    }

    public record RequestBreakdown(
            ObjectId id,
            String requestName, // e.g., "Grooming: Premium Wash & Cut"
            double total, // The total price for that request
            Instant createdAt
    ) {

        public static RequestBreakdown createNew(String id, String requestName, double total, Instant time) {
            if (!ObjectId.isValid(id)) throw new DomainArgumentException("Request id cannot be instantiated as ObjectId");
            if (requestName == null || requestName.trim().isEmpty()) throw new DomainArgumentException("Request name cannot be empty");
            if (total <= 0) throw new DomainArgumentException("Total price must be greater than 0");
            if (time == null) throw new DomainArgumentException("Creation time cannot be null");
            return new RequestBreakdown(new ObjectId(id), requestName, total, time);
        }
    }
}
