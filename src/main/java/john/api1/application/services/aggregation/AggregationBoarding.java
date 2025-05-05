package john.api1.application.services.aggregation;

import john.api1.application.domain.cores.boarding.BoardingPricingDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.boarding.BoardingCreatedDTO;
import john.api1.application.dto.mapper.boarding.BoardingDTO;
import john.api1.application.dto.mapper.boarding.RequestBreakdownDTO;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.services.IBoardingAggregation;
import john.api1.application.ports.services.media.IMediaSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class AggregationBoarding implements IBoardingAggregation {
    private final IMediaSearch mediaSearch;

    @Autowired
    public AggregationBoarding(IMediaSearch mediaSearch) {
        this.mediaSearch = mediaSearch;
    }

    @Override
    public BoardingCreatedDTO boardingCreatedAggregation(BoardingDomain boarding, BoardingPricingDomain pricing, PetOwnerCQRS owner, PetCQRS pet, Instant created) {
        var photoUrl = mediaSearch.findProfilePicByOwnerId(boarding.getPetId());
        String photoId = null;
        String mediaUrl = null;
        Instant expireAt = null;

        if (photoUrl.isPresent()) {
            photoId = photoUrl.get().id();
            mediaUrl = photoUrl.get().mediaUrl();
            expireAt = photoUrl.get().expireAt();
        }
        return new BoardingCreatedDTO(
                // id
                boarding.getId(), boarding.getPetId(), boarding.getOwnerId(),
                // pet
                photoId, mediaUrl, expireAt,
                pet.petName(), pet.animalType(), pet.breed(), pet.size(), pet.age(),
                // owner
                owner.ownerName(), owner.ownerEmail(), owner.ownerPhoneNumber(),
                String.join(", ", owner.streetAddress(), owner.cityAddress(), owner.stateAddress()),
                // boarding
                boarding.getBoardingType().getBoardingType(),
                boarding.getBoardingStart(),
                boarding.getBoardingEnd(),
                boarding.getPaymentStatus().getPaymentStatus(),
                boarding.getNotes(),
                // pricing breakdown
                pricing.getRatePerHour(),
                BoardingPricingDS.getBoardingTotal(pricing),
                BoardingPricingDS.getOverallTotal(pricing),
                created
        );
    }

    public BoardingDTO boardingReleasedAggregation(BoardingDomain boarding, BoardingPricingDomain pricing, PetOwnerCQRS owner, PetCQRS pet, long durationDays, long durationHours, Instant extensionTime, Instant releasedAt) {
        var photoUrl = mediaSearch.findProfilePicByOwnerId(boarding.getPetId());
        String photoId = null;
        String mediaUrl = null;
        Instant expireAt = null;

        if (photoUrl.isPresent()) {
            photoId = photoUrl.get().id();
            mediaUrl = photoUrl.get().mediaUrl();
            expireAt = photoUrl.get().expireAt();
        }
        return new BoardingDTO(
                // id
                boarding.getId(), boarding.getPetId(), boarding.getOwnerId(),
                // pet
                photoId, mediaUrl, expireAt,
                pet.petName(), pet.animalType(), pet.breed(), pet.size(), pet.age(),
                // owner
                owner.ownerName(), owner.ownerEmail(), owner.ownerPhoneNumber(),
                String.join(", ", owner.streetAddress(), owner.cityAddress(), owner.stateAddress()),
                // boarding details
                boarding.getBoardingStatus().getBoardingStatus(), boarding.getBoardingType().getBoardingType(),
                boarding.getBoardingStart(), boarding.getBoardingEnd(), extensionTime, releasedAt,
                durationDays, durationHours, boarding.getNotes(),
                // pricing
                boarding.getPaymentStatus().getPaymentStatus(),
                pricing.getRatePerHour(),
                BoardingPricingDS.getBoardingTotal(pricing),
                RequestBreakdownDTO.map(pricing.getRequestBreakdown()),
                BoardingPricingDS.getOverallTotal(pricing),
                boarding.getCreatedAt()
        );
    }

    public BoardingDTO boardingAggregation(BoardingDomain boarding, BoardingPricingDomain pricing, PetOwnerCQRS owner, PetCQRS pet, long durationDays, long durationHours, Instant extensionTime) {
        var photoUrl = mediaSearch.findProfilePicByOwnerId(boarding.getPetId());
        String photoId = null;
        String mediaUrl = null;
        Instant expireAt = null;

        Instant extension = null;

        if (photoUrl.isPresent()) {
            photoId = photoUrl.get().id();
            mediaUrl = photoUrl.get().mediaUrl();
            expireAt = photoUrl.get().expireAt();
        }

        if(extensionTime != null) {
            extension = extensionTime;
        }

        return new BoardingDTO(
                // id
                boarding.getId(), boarding.getPetId(), boarding.getOwnerId(),
                // pet
                photoId, mediaUrl, expireAt,
                pet.petName(), pet.animalType(), pet.breed(), pet.size(), pet.age(),
                // owner
                owner.ownerName(), owner.ownerEmail(), owner.ownerPhoneNumber(),
                String.join(", ", owner.streetAddress(), owner.cityAddress(), owner.stateAddress()),
                // boarding details
                boarding.getBoardingStatus().getBoardingStatus(), boarding.getBoardingType().getBoardingType(),
                boarding.getBoardingStart(), boarding.getBoardingEnd(), extension, null,
                durationDays, durationHours, boarding.getNotes(),
                // pricing
                boarding.getPaymentStatus().getPaymentStatus(),
                pricing.getRatePerHour(),
                BoardingPricingDS.getBoardingTotal(pricing),
                RequestBreakdownDTO.map(pricing.getRequestBreakdown()),
                BoardingPricingDS.getOverallTotal(pricing),
                boarding.getCreatedAt()
        );
    }


}
