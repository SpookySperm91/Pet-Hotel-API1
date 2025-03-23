package john.api1.application.domain.models.boarding;

import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class ExtensionDomain {
    private final String id;
    private final String requestId;
    private final String boardingId;
    private double additionalPrice;
    private Instant extendedTime;
    private String description;

    public void setAdditionalPrice(Instant extendedTime) {
        //
    }

}
