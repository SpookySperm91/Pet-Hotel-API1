package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.GroomingType;
import john.api1.application.components.enums.PetPrices;
import john.api1.application.components.enums.PetSize;
import john.api1.application.components.enums.SpeciesType;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.boarding.BoardingExtensionDS;
import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.dto.request.request.RequestExtensionRDTO;
import john.api1.application.dto.request.request.RequestGroomingRDTO;
import john.api1.application.dto.request.request.RequestMediaRDTO;
import john.api1.application.ports.repositories.request.IRequestCreateRepository;
import john.api1.application.ports.services.IPetOwnerManagement;
import john.api1.application.ports.services.IRequestAggregation;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestCreate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestCreateAS implements IRequestCreate {
    private final IPetOwnerManagement petOwnerManagement;
    private final IPetSearch petSearch;
    private final IBoardingSearch boardingSearch;
    private final IRequestCreateRepository requestCreate;
    private final IRequestAggregation aggregation;

    @Autowired
    public RequestCreateAS(IPetOwnerManagement petOwnerManagement,
                           IPetSearch petSearch,
                           IBoardingSearch boardingSearch,
                           IRequestCreateRepository requestCreate,
                           IRequestAggregation aggregation) {
        this.petOwnerManagement = petOwnerManagement;
        this.petSearch = petSearch;
        this.boardingSearch = boardingSearch;
        this.requestCreate = requestCreate;
        this.aggregation = aggregation;
    }


    // Check if current boarding is active
    // Create media request. Save to DB
    // Retrieve pet and owner names for DTO
    // Aggregation to DTO
    // Return response
    @Override
    public DomainResponse<RequestMediaCreatedDTO> createRequestMedia(RequestMediaRDTO request) {
        try {
            validateId(request.getOwnerId(), request.getPetId(), request.getBoardingId());

            var active = boardingSearch.isBoardingActive(request.getBoardingId());
            if (!active.isSuccess()) return DomainResponse.error(active.getMessage());

            // Create new request and save to DB
            RequestType type = RequestType.fromString(request.getRequestType());
            RequestDomain domain = RequestDomain.create(request.getPetId(), request.getOwnerId(), request.getBoardingId(), type, RequestStatus.PENDING, request.getDescription());
            domain = saveRequest(domain);

            // Prepare DTO
            String ownerName = petOwnerManagement.getPetOwnerName(request.getOwnerId());
            String petName = petSearch.getPetName(request.getPetId());
            var dto = aggregation.requestCreateMediaAggregation(domain, ownerName, petName);

            return DomainResponse.success(dto, "New media request for pet '" + petName + "' is successfully created. Currently pending to be approved!");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }


    // Check if current boarding is active
    // Create extension request. Save to DB
    // Retrieve data needed for calculation and DTO
    // Calculate the additional price
    // Create extension domain and persist to DB as inactive
    // Aggregation to DTO
    // Return response
    @Override
    public DomainResponse<RequestExtensionCreatedDTO> createRequestExtension(RequestExtensionRDTO request) {
        try {
            validateId(request.getOwnerId(), request.getPetId(), request.getBoardingId());

            var active = boardingSearch.isBoardingActive(request.getBoardingId());
            if (!active.isSuccess()) return DomainResponse.error(active.getMessage());

            // Create new request and save to DB
            RequestDomain domain = RequestDomain.create(request.getPetId(), request.getOwnerId(), request.getBoardingId(), RequestType.BOARDING_EXTENSION, RequestStatus.PENDING, request.getDescription());
            domain = saveRequest(domain);

            // Fetch data needed for calculation and DTO
            var pet = petSearch.getPetNameBreedSize(request.getPetId());
            String ownerName = petOwnerManagement.getPetOwnerName(request.getOwnerId());

            // Determine extension hours and calculate additional price
            BoardingType type = BoardingExtensionDS.extensionType(request.getExtensionType());
            long extendedHours = BoardingExtensionDS.calculateExtendedHours(type, request.getExtensionDuration());

            PetPrices priceRate = PetPrices.fromSpeciesAndSize(
                    SpeciesType.fromStringToSpecies(pet.animalType()),
                    PetSize.fromStringToSize(pet.size()));

            // Create and persist extension domain
            ExtensionDomain extensionDomain = new ExtensionDomain(
                    domain.getId(),
                    request.getBoardingId(),
                    request.getDescription());
            extensionDomain.setAdditionalPrice(priceRate, extendedHours);
            requestCreate.createInitialRequestExtension(extensionDomain);

            // Aggregation DTO
            var dto = aggregation.requestCreateExtensionAggregation(domain, ownerName, pet.petName(), extensionDomain.getExtendedHours(), request.getExtensionType());

            return DomainResponse.success(dto, "New extension request for pet '" + pet.petName() + "' is successfully created. Currently pending to be approved!");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    // Check if current boarding is active
    // Create grooming request. Save to DB
    // Retrieve data needed for calculation and DTO
    // Calculate the grooming price per pet's size
    // Create grooming domain and persist to DB as inactive
    // Aggregation to DTO
    // Return response
    @Override
    public DomainResponse<RequestGroomingCreatedDTO> createRequestGrooming(RequestGroomingRDTO request) {
        try {
            validateId(request.getOwnerId(), request.getPetId(), request.getBoardingId());

            var active = boardingSearch.isBoardingActive(request.getBoardingId());
            if (!active.isSuccess()) return DomainResponse.error(active.getMessage());

            // Create new request and save to DB
            RequestDomain domain = RequestDomain.create(request.getPetId(), request.getOwnerId(), request.getBoardingId(), RequestType.GROOMING_SERVICE, RequestStatus.PENDING, request.getDescription());
            domain = saveRequest(domain);

            // Fetch data needed for calculation and DTO
            var pet = petSearch.getPetNameBreedSize(request.getPetId());
            String ownerName = petOwnerManagement.getPetOwnerName(request.getOwnerId());

            // Determine grooming type and pricing per pet's size
            GroomingType groomingType = GroomingType.safeFromStringOrDefault(request.getGroomingService());
            PetPrices priceRate = PetPrices.fromSpeciesAndSize(
                    SpeciesType.fromStringToSpecies(pet.animalType()),
                    PetSize.fromStringToSize(pet.size()));

            // Create and persist extension domain
            GroomingDomain groomingDomain = new GroomingDomain(
                    domain.getId(),
                    request.getBoardingId(),
                    request.getDescription());
            groomingDomain.setGroomingAndPrice(groomingType, priceRate);
            requestCreate.createInitialRequestGrooming(groomingDomain);

            // Aggregation DTO
            var dto = aggregation.requestCreateGroomingAggregation(domain, ownerName, pet.petName(), groomingDomain.getGroomingPrice(), pet.size());

            return DomainResponse.success(dto, "New grooming request for pet '" + pet.petName() + "' is successfully created. Currently pending to be approved!");

        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    private RequestDomain saveRequest(RequestDomain domain) {
        var create = requestCreate.createRequest(domain);
        if (create.isEmpty()) throw new PersistenceException("Failed to create new request");
        return domain.withId(create.get());
    }


    private void validateId(String ownerId, String petId, String boardingId) {
        if (!ObjectId.isValid(ownerId))
            throw new PersistenceException("Owner id cannot be converted into ObjectId");
        if (!ObjectId.isValid(petId))
            throw new PersistenceException("Pet id cannot be converted into ObjectId");
        if (!ObjectId.isValid(boardingId))
            throw new PersistenceException("boarding id cannot be converted into ObjectId");
    }


}
