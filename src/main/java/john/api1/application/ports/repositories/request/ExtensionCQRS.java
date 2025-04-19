package john.api1.application.ports.repositories.request;

import jakarta.annotation.Nullable;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.domain.models.request.ExtensionDomain;

public record ExtensionCQRS(
        long extendedHours,
        double additionalPrice,
        BoardingType durationType
) {
}
