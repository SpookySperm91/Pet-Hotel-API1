package john.api1.application.adapters.repositories;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pricing-breakdown")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricingEntity {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId boardingId;
    private double ratePerHour; // Standard rate per hour
    private String boardingType; // "Daycare" or "Long-stay"
    private long boardingDuration; // Hours if Daycare, Days if Long-stay
    private boolean isPrepaid; // True if the boarding is prepaid and pricing should be excluded
    private List<BoardingPricingDomain.RequestBreakdown> requestBreakdown; // List of additional service requests
}
