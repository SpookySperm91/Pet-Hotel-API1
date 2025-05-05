package john.api1.application.ports.services;

import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.boarding.BoardingCreatedDTO;
import john.api1.application.dto.mapper.boarding.BoardingDTO;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;

import java.time.Instant;

public interface IBoardingAggregation {
    BoardingCreatedDTO boardingCreatedAggregation(BoardingDomain boarding, BoardingPricingDomain pricing, PetOwnerCQRS owner, PetCQRS pet, Instant created);

    BoardingDTO boardingReleasedAggregation(BoardingDomain boarding, BoardingPricingDomain pricing, PetOwnerCQRS owner, PetCQRS pet, long durationDays, long durationHours, Instant extensionTime, Instant releasedAt);

    BoardingDTO boardingAggregation(BoardingDomain boarding, BoardingPricingDomain pricing, PetOwnerCQRS owner, PetCQRS pet, long durationDays, long durationHours, Instant extensionTime);

}
