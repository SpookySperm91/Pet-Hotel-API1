package john.api1.application.services.user;

import john.api1.application.components.DateUtils;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.dto.mapper.owner.PetOwnerDTO;
import john.api1.application.dto.mapper.owner.PetOwnerPendingDTO;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.IPetOwnerSearchAggregation;
import john.api1.application.ports.services.pet.IPetSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetOwnerSearchAggregationAS implements IPetOwnerSearchAggregation {
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;

    @Autowired
    public PetOwnerSearchAggregationAS(IPetOwnerSearch ownerSearch,
                                       IPetSearch petSearch) {
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
    }

    @Override
    public DomainResponse<List<PetOwnerDTO>> searchAllActive() {
        try {
            var allActive = ownerSearch.getAllActivePetOwner();

            List<PetOwnerDTO> result = allActive.stream()
                    .map(owner -> {
                        var pets = petSearch.getAllByOwner(owner.id());
                        return mapToDTO(owner, pets);
                    })
                    .toList();

            return DomainResponse.success(result, "Successfully retrieved active owners");
        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something went wrong. Please try again later.");
        }
    }

    @Override
    public DomainResponse<PetOwnerDTO> searchRecent() {
        try {
            var optionalOwner = ownerSearch.getRecentActivePetOwner();
            var pets = petSearch.getAllByOwner(optionalOwner.id());

            PetOwnerDTO dto = mapToDTO(optionalOwner, pets);
            return DomainResponse.success(dto, "Successfully retrieved the most recent active pet owner");

        } catch (PersistenceException e) {
            // no recent active
            return DomainResponse.success(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something went wrong. Please try again later.");
        }
    }

    @Override
    public DomainResponse<PetOwnerDTO> searchById(String id) {
        try {
            var ow = ownerSearch.getPetOwnerDetails(id);
            if (ow.isEmpty()) {
                return DomainResponse.error("Pet owner cannot be found");
            }

            var owner = ow.get();
            var pets = petSearch.getAllByOwner(owner.id());

            PetOwnerDTO dto = mapToDTO(owner, pets);
            return DomainResponse.success(dto, "Successfully retrieved pet owner");

        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something went wrong. Please try again later.");
        }
    }

    // PENDING SHIT
    @Override
    public DomainResponse<List<PetOwnerPendingDTO>> searchAllPending() {
        try {
            var allActive = ownerSearch.getAllPendingPetOwner();

            List<PetOwnerPendingDTO> result = allActive.stream()
                    .map(this::mapToDTO)
                    .toList();

            return DomainResponse.success(result, "Successfully retrieved active owners");
        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something went wrong. Please try again later.");
        }
    }

    @Override
    public DomainResponse<PetOwnerPendingDTO> searchRecentPending() {
        try {
            var optionalOwner = ownerSearch.getRecentPendingPetOwner();
            var dto = mapToDTO(optionalOwner);
            return DomainResponse.success(dto, "Successfully retrieved the most recent active pet owner");

        } catch (PersistenceException e) {
            // no recent active
            return DomainResponse.success(e.getMessage());
        } catch (Exception e) {
            return DomainResponse.error("Something went wrong. Please try again later.");
        }
    }


    // DRY utility method
    private PetOwnerDTO mapToDTO(PetOwnerCQRS owner, List<PetCQRS> pets) {
        var petDTOs = pets.stream()
                .map(pet -> new PetOwnerDTO.PetDTO(
                        pet.id(),
                        pet.petName(),
                        pet.animalType(),
                        pet.breed(),
                        pet.size(),
                        pet.age(),
                        pet.boarding()
                )).toList();

        int currentlyBoarding = (int) pets.stream().filter(PetCQRS::boarding).count();

        return new PetOwnerDTO(
                owner.id(),
                owner.ownerName(),
                owner.ownerEmail(),
                owner.ownerPhoneNumber(),
                String.format("%s, %s, %s", owner.streetAddress(), owner.cityAddress(), owner.stateAddress()),
                petDTOs,
                DateUtils.formatInstant(owner.createdAt()),
                currentlyBoarding
        );
    }

    private PetOwnerPendingDTO mapToDTO(PetOwnerCQRS owner) {
        return new PetOwnerPendingDTO(
                owner.id(),
                owner.ownerName(),
                owner.ownerEmail(),
                owner.ownerPhoneNumber(),
                String.format("%s, %s, %s", owner.streetAddress(), owner.cityAddress(), owner.stateAddress()),
                DateUtils.formatInstantWithTime(owner.createdAt()));
    }
}
