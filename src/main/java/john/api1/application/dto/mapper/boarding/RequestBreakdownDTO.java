package john.api1.application.dto.mapper.boarding;

import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public record RequestBreakdownDTO(String id,
                                  String requestName, // e.g., "Grooming: Premium Wash & Cut"
                                  double total, // The total price for that request
                                  Instant createdAt) {

    public static List<RequestBreakdownDTO> map(List<BoardingPricingDomain.RequestBreakdown> breakdowns) {
        return breakdowns.stream()
                .map(b -> new RequestBreakdownDTO(
                        b.id().toString(),  // Convert ObjectId to String
                        b.requestName(),
                        b.total(),
                        b.createdAt()
                ))
                .collect(Collectors.toList());
    }
}
