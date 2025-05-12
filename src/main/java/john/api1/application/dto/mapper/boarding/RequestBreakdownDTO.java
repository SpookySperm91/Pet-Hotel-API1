package john.api1.application.dto.mapper.boarding;

import john.api1.application.components.DateUtils;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;

import java.util.List;
import java.util.stream.Collectors;

public record RequestBreakdownDTO(String id,
                                  String requestName, // e.g., "Grooming: Premium Wash & Cut"
                                  double total, // The total price for that request
                                  String createdAt) {

    public static List<RequestBreakdownDTO> map(List<BoardingPricingDomain.RequestBreakdown> breakdowns) {
        return breakdowns.stream()
                .map(b -> new RequestBreakdownDTO(
                        b.id().toString(),  // Convert ObjectId to String
                        b.requestName(),
                        b.total(),
                        DateUtils.formatInstantWithTime(b.createdAt())
                ))
                .collect(Collectors.toList());
    }
}
