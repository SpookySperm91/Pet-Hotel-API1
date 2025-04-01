package john.api1.application.dto.mapper.boarding;

import java.util.List;

// DEDICATED FOR PRICE BREAKDOWN
public record BoardingPricingDTO(
        double ratePerHour, // Standard rate per hour
        double ratePerDay, // Standard rate per day (could be ratePerHour * 24 if needed)

        String boardingType, // "Daycare" or "Longstay"
        double boardingDuration, // Hours if Daycare, Days if Longstay
        double boardingTotal, // Computed total based on type

        boolean boardingPaid, // If true, exclude from overallTotal

        List<RequestBreakdown> requestBreakdown, // List of additional service requests
        double requestTotal, // Total cost of all requests

        double overallTotal // Final total (boardingTotal + requestTotal, unless boardingPaid)
) {
    public static BoardingPricingDTO calculate(
            String boardingType,
            double ratePerHour,
            double duration, // Hours for daycare, Days for long-stay
            boolean boardingPaid,
            List<RequestBreakdown> requestBreakdown) {

        double ratePerDay = ratePerHour * 24;
        double boardingTotal = boardingType.equals("Daycare") ? ratePerHour * duration : ratePerDay * duration;

        double requestTotal = requestBreakdown.stream().mapToDouble(RequestBreakdown::total).sum();
        double overallTotal = boardingPaid ? requestTotal : (boardingTotal + requestTotal);

        return new BoardingPricingDTO(
                ratePerHour,
                ratePerDay,
                boardingType,
                duration,
                boardingTotal,
                boardingPaid,
                requestBreakdown,
                requestTotal,
                overallTotal
        );
    }

    public record RequestBreakdown(
            String requestName, // e.g., "Grooming: Premium Wash & Cut"
            double total // The total price for that request
    ) {
    }

}
