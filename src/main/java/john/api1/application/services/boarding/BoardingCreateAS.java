package john.api1.application.services.boarding;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.PetPrices;
import john.api1.application.components.enums.PetSize;
import john.api1.application.components.enums.SpeciesType;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.boarding.BoardingPricingDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.boarding.BoardingPricingDomain;
import john.api1.application.dto.mapper.boarding.BoardingCreatedDTO;
import john.api1.application.dto.request.BoardingRDTO;
import john.api1.application.ports.repositories.boarding.IBoardingCreateRepository;
import john.api1.application.ports.repositories.boarding.IPricingManagementRepository;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.services.IPetOwnerManagement;
import john.api1.application.ports.services.boarding.IBoardingCreate;
import john.api1.application.ports.services.pet.IPetSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BoardingCreateAS implements IBoardingCreate {
    private final IBoardingCreateRepository createRepository;
    private final IPricingManagementRepository pricingManagement;
    private final IPetSearch pet;
    private final IPetOwnerManagement petOwner;


    @Autowired
    public BoardingCreateAS(IBoardingCreateRepository createRepository,
                            IPricingManagementRepository pricingManagement,
                            IPetSearch pet,
                            IPetOwnerManagement petOwner) {
        this.createRepository = createRepository;
        this.pricingManagement = pricingManagement;
        this.pet = pet;
        this.petOwner = petOwner;
    }

    // Validates if boarding time is appropriate
    // Check pet if currently boarding
    // Get pet and owner data for aggregation
    // Create boarding domain object
    // Create pricing domain object
    // Save to DB
    // Aggregate necessary values to convert into dto
    // Return response
    public DomainResponse<BoardingCreatedDTO> createNewBoarding(BoardingRDTO boardingRequest,
                                                                BoardingType boardingType,
                                                                PaymentStatus paymentStatus) {
        try {
            String petId = boardingRequest.getPetId();
            String ownerId = boardingRequest.getOwnerId();
            Instant start = boardingRequest.getBoardingStart();
            Instant end = boardingRequest.getBoardingEnd();

            // Validation
            if (start.isAfter(end))
                return DomainResponse.error("Boarding start date cannot be after end date");

            // Retrieve values for aggregation
            var petDetails = pet.getPetBoardingDetails(petId);
            if (petDetails.boarding())
                return DomainResponse.error("Pet is currently boarding");
            var owner = petOwner.getPetOwnerBoardingDetails(ownerId);

            // Boarding creation
            BoardingDomain boarding = createBoarding(boardingRequest, boardingType, paymentStatus);
            String boardingId = saveBoarding(boarding);

            // Pricing breakdown creation
            BoardingPricingDomain pricing = calculatePricing(boardingId, petDetails, boardingType, boarding.getBoardingDuration(), paymentStatus);
            pricingManagement.save(pricing);

            // Returns aggregated dto response
            return createSuccessResponse(boarding.getId(), boardingRequest, petDetails, owner, pricing, boardingType, paymentStatus, boarding.getCreatedAt());

        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (MongoException e) {
            return DomainResponse.error("A database error occurred. Please try again.");
        }
    }

    // Creates boarding domain
    private BoardingDomain createBoarding(BoardingRDTO boardingRequest,
                                          BoardingType boardingType,
                                          PaymentStatus paymentStatus) {
        return BoardingDomain.create(
                boardingRequest.getPetId(),
                boardingRequest.getOwnerId(),
                boardingType,
                boardingRequest.getBoardingStart(),
                boardingRequest.getBoardingEnd(),
                paymentStatus,
                boardingRequest.getNotes()
        );
    }

    // Save boarding to DB
    private String saveBoarding(BoardingDomain boarding) {
        return createRepository.saveBoarding(boarding);
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

    // Create success response
    private DomainResponse<BoardingCreatedDTO> createSuccessResponse(String boardingId,
                                                                     BoardingRDTO boardingRequest,
                                                                     PetCQRS pet,
                                                                     PetOwnerCQRS owner,
                                                                     BoardingPricingDomain pricing,
                                                                     BoardingType boardingType,
                                                                     PaymentStatus paymentStatus,
                                                                     Instant createdAt) {

        return DomainResponse.success(
                new BoardingCreatedDTO(
                        // id
                        boardingId, boardingRequest.getPetId(), boardingRequest.getOwnerId(),
                        // pet
                        pet.petName(), pet.animalType(), pet.breed(), pet.size(), pet.age(),
                        // owner
                        owner.ownerName(), owner.ownerEmail(), owner.ownerPhoneNumber(),
                        String.join(", ", owner.streetAddress(), owner.cityAddress(), owner.stateAddress()),
                        // boarding
                        boardingType.getBoardingType(),
                        boardingRequest.getBoardingStart(),
                        boardingRequest.getBoardingEnd(),
                        paymentStatus.getPaymentStatus(),
                        boardingRequest.getNotes(),
                        // pricing breakdown
                        BoardingPricingDS.getBoardingTotal(pricing),
                        pricing.getRequestBreakdown(),
                        BoardingPricingDS.getFinalTotal(pricing),
                        createdAt
                ),
                "Pet '" + pet.petName() + "' successfully boarded"
        );
    }
}
