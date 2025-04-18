package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.PetPrices;
import john.api1.application.components.enums.PetSize;
import john.api1.application.components.enums.SpeciesType;
import john.api1.application.components.enums.boarding.BoardingStatus;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.boarding.BoardingCreatedDTO;
import john.api1.application.dto.request.BoardingRDTO;
import john.api1.application.ports.repositories.boarding.IBoardingCreateRepository;
import john.api1.application.ports.repositories.boarding.IPricingManagementRepository;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.services.IBoardingAggregation;
import john.api1.application.ports.services.IPetOwnerManagement;
import john.api1.application.ports.services.boarding.IBoardingCreate;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.pet.IPetUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BoardingCreateAS implements IBoardingCreate {
    private final IBoardingCreateRepository createRepository;
    private final IPricingManagementRepository pricingManagement;
    private final IPetSearch pet;
    private final IPetUpdate petUpdate;
    private final IPetOwnerManagement petOwner;
    private final IBoardingAggregation aggregation;


    @Autowired
    public BoardingCreateAS(IBoardingCreateRepository createRepository,
                            IPricingManagementRepository pricingManagement,
                            IPetSearch pet,
                            IPetUpdate petUpdate,
                            IPetOwnerManagement petOwner,
                            IBoardingAggregation aggregation) {
        this.createRepository = createRepository;
        this.pricingManagement = pricingManagement;
        this.pet = pet;
        this.petUpdate = petUpdate;
        this.petOwner = petOwner;
        this.aggregation = aggregation;
    }

    // Validates if boarding time is appropriate
    // Check pet if currently boarding
    // Get pet and owner data for aggregation
    // Create boarding domain object
    // Create pricing domain object
    // Update pet boarding status
    // Save to DB
    // Aggregate necessary values to convert into dto
    // Return response
    public DomainResponse<BoardingCreatedDTO> createNewBoarding(BoardingRDTO boardingRequest,
                                                                BoardingType boardingType,
                                                                PaymentStatus paymentStatus,
                                                                Instant startAt,
                                                                Instant endAt) {
        try {
            String petId = boardingRequest.getPetId();
            String ownerId = boardingRequest.getOwnerId();

            // Validation
            if (startAt.isAfter(endAt)) return DomainResponse.error("Boarding start date cannot be after end date");

            // Check pet and owner for extra validation
            var petExist = petOwner.safeVerifyPetOwnership(ownerId, petId);
            if (!petExist.isSuccess()) return DomainResponse.error(petExist.getMessage());

            var petDetails = pet.getPetBoardingDetails(petId);
            if (petDetails.boarding()) return DomainResponse.error("Pet is currently boarding");

            var owner = petOwner.getPetOwnerBoardingDetails(ownerId);

            // Boarding creation and to DB
            BoardingDomain boarding = createBoarding(boardingRequest, boardingType, paymentStatus, startAt, endAt);
            String boardingId = createRepository.saveBoarding(boarding);
            boarding = boarding.withId(boardingId);
            // update pet boarding as active
            petUpdate.updatePetStatus(petId, BoardingStatus.BOARDING);

            // Pricing breakdown creation
            BoardingPricingDomain pricing = calculatePricing(boardingId, petDetails, boardingType, boarding.getBoardingDuration(), paymentStatus);
            pricingManagement.save(pricing);

            // Returns aggregated dto response
            var dto = aggregation.boardingCreatedAggregation(boarding, pricing, owner, petDetails, boarding.getCreatedAt());
            return DomainResponse.success(dto, "Pet '" + petDetails.petName() + "' successfully boarded");

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("A database error occurred. Please try again.");
        }
    }

    // Creates boarding domain
    private BoardingDomain createBoarding(BoardingRDTO boardingRequest,
                                          BoardingType boardingType,
                                          PaymentStatus paymentStatus,
                                          Instant startAt, Instant endAt) {
        return BoardingDomain.create(
                boardingRequest.getPetId(),
                boardingRequest.getOwnerId(),
                boardingType,
                startAt, endAt,
                paymentStatus,
                boardingRequest.getNotes()
        );
    }

    // Calculate pricing for boarding
    // Create domain object
    private BoardingPricingDomain calculatePricing(String boardingId,
                                                   PetCQRS pet,
                                                   BoardingType boardingType,
                                                   long time,
                                                   PaymentStatus paymentStatus) {
        PetPrices rate = PetPrices.fromSpeciesAndSize(
                SpeciesType.fromStringToSpecies(pet.animalType()),
                PetSize.fromStringToSize(pet.size()));

        return BoardingPricingDomain.createNew(
                boardingId,
                rate.getBoardingPrice(),
                boardingType,
                time,
                paymentStatus.equals(PaymentStatus.PAID)
        );
    }
}

