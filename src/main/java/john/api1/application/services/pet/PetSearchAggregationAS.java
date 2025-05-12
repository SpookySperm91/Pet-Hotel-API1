package john.api1.application.services.pet;

import john.api1.application.components.DateUtils;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.RequestType;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.boarding.BoardingPricingDS;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.mapper.pet.PetBoardingHistoryDTO;
import john.api1.application.dto.mapper.pet.PetDetailsDTO;
import john.api1.application.dto.mapper.request.search.PetRequestHistoryDTO;
import john.api1.application.ports.repositories.boarding.IBoardingSearchRepository;
import john.api1.application.ports.repositories.request.IRequestCompletedSearchRepository;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.repositories.request.RequestCQRS;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.boarding.IPricingManagement;
import john.api1.application.ports.services.media.IMediaSearch;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.pet.IPetSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PetSearchAggregationAS implements IPetSearchResponse {
    private final IPetSearch petSearch;
    private final IMediaSearch mediaSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IBoardingSearchRepository boardingSearch;
    private final IPricingManagement boardingPricing;
    private final IRequestSearchRepository requestSearch;
    private final IRequestCompletedSearchRepository requestPricing;

    @Autowired
    public PetSearchAggregationAS(IPetSearch petSearch,
                                  IMediaSearch mediaSearch,
                                  IPetOwnerSearch ownerSearch,
                                  IBoardingSearchRepository boardingSearch,
                                  IPricingManagement boardingPricing,
                                  IRequestSearchRepository requestSearch,
                                  IRequestCompletedSearchRepository requestPricing) {
        this.petSearch = petSearch;
        this.mediaSearch = mediaSearch;
        this.ownerSearch = ownerSearch;
        this.boardingSearch = boardingSearch;
        this.boardingPricing = boardingPricing;
        this.requestSearch = requestSearch;
        this.requestPricing = requestPricing;
    }

    @Override
    public DomainResponse<List<PetDetailsDTO>> searchAll() {
        var pets = petSearch.getAll();
        if (pets.isEmpty()) throw new PersistenceException("No pets are retrieved");

        var dto = new PetDetailsDTO[pets.size()];

        List<PetDetailsDTO> dtoList = new ArrayList<>();

        for (var pet : pets) {
            List<PetBoardingHistoryDTO> boardingHistory = new ArrayList<>();
            List<PetRequestHistoryDTO> requestHistory = new ArrayList<>();
            MediaIdUrlExpire media = null;
            String ownerName;

            try {
                ownerName = ownerSearch.getPetOwnerName(pet.ownerId());
            } catch (PersistenceException e) {
                continue; // Skip this pet if owner name cannot be retrieved
            }

            try {
                var boarding = boardingSearch.searchAllCompletedByPetId(pet.id());
                boardingHistory = getBoardingData(boarding);
            } catch (Exception e) {
                // Ignore and continue
            }

            try {
                var requests = requestSearch.findAllCompletedByPetId(pet.id());
                requestHistory = getRequestData(requests);
            } catch (Exception e) {
                // Ignore and continue
            }

            try {
                media = mediaSearch.findProfilePicByOwnerId(pet.id()).orElse(null);
            } catch (Exception e) {
                // Optional media, continue silently
            }

            dtoList.add(PetDetailsDTO.map(
                    pet,
                    media != null ? media : new MediaIdUrlExpire(null, null, null),
                    pet.ownerId(),
                    ownerName,
                    boardingHistory,
                    requestHistory
            ));
        }

        return DomainResponse.success(dtoList, "Successfully retrieve " + dto.length + " pets");
    }


    @Override
    public DomainResponse<PetDetailsDTO> searchRecent() {
        try {
            var pet = petSearch.getRecent();

            List<PetBoardingHistoryDTO> boardingHistory = new ArrayList<>();
            List<PetRequestHistoryDTO> requestHistory = new ArrayList<>();
            MediaIdUrlExpire media = null;
            String ownerName;

            try {
                ownerName = ownerSearch.getPetOwnerName(pet.ownerId());
            } catch (PersistenceException e) {
                ownerName = "N/A";
            }

            try {
                var boarding = boardingSearch.searchAllCompletedByPetId(pet.id());
                boardingHistory = getBoardingData(boarding);
            } catch (Exception e) {
                // Ignore and continue
            }

            try {
                var requests = requestSearch.findAllCompletedByPetId(pet.id());
                requestHistory = getRequestData(requests);
            } catch (Exception e) {
                // Ignore and continue
            }

            try {
                media = mediaSearch.findProfilePicByOwnerId(pet.id()).orElse(null);
            } catch (Exception e) {
                // Optional media, continue silently
            }

            return DomainResponse.success(PetDetailsDTO.map(
                    pet,
                    media != null ? media : new MediaIdUrlExpire(null, null, null),
                    pet.ownerId(),
                    ownerName,
                    boardingHistory,
                    requestHistory
            ), "Successfully retrieve recent pet details");
        } catch (PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        }
    }


        private List<PetBoardingHistoryDTO> getBoardingData (List < BoardingDomain > boarding) {
            PetBoardingHistoryDTO[] data = new PetBoardingHistoryDTO[boarding.size()];

            for (int i = 0; i < boarding.size(); i++) {
                String checkIn = DateUtils.formatInstant(boarding.get(i).getBoardingStart());
                String checkOut = DateUtils.formatInstant(boarding.get(i).getUpdatedAt());

                var priceDetails = boardingPricing.getBoardingPricingCqrs(boarding.get(i).getId());
                Double price = BoardingPricingDS.getBoardingTotal(priceDetails.get());
                String duration = switch (priceDetails.get().type()) {
                    case DAYCARE -> priceDetails.get().rate() + " Hours";
                    case LONG_STAY -> priceDetails.get().rate() + " Days";
                };

                data[i] = new PetBoardingHistoryDTO(
                        boarding.get(i).getId(),
                        boarding.get(i).getBoardingType().getDurationType(),
                        duration,
                        boarding.get(i).getNotes(),
                        price,
                        boarding.get(i).getPaymentStatus().getPaymentStatusDto(),
                        checkIn, checkOut
                );

            }

            return Arrays.stream(data).toList();
        }

        private List<PetRequestHistoryDTO> getRequestData (List < RequestCQRS > request) {

            PetRequestHistoryDTO[] data = new PetRequestHistoryDTO[request.size()];

            for (int i = 0; i < request.size(); i++) {
                String createdAt = DateUtils.formatInstant(request.get(i).createdAt());

                Double price = switch (request.get(i).type()) {
                    case RequestType.BOARDING_EXTENSION ->
                            requestPricing.getExtensionPriceByRequestId(request.get(i).id()).get();
                    case RequestType.GROOMING_SERVICE ->
                            requestPricing.getGroomingPriceByRequestId(request.get(i).id()).get();
                    default -> null;
                };

                data[i] = new PetRequestHistoryDTO(
                        request.get(i).id(),
                        request.get(i).type().getRequestTypeToDto(),
                        createdAt,
                        request.get(i).description(),
                        price != null ? price : 0,
                        price != null ? "Paid" : "N/A",
                        "Completed"
                );

            }
            return Arrays.stream(data).toList();
        }
    }
