package john.api1.application.services.request;

import john.api1.application.components.DateUtils;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.boarding.RequestStatus;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.dto.mapper.request.search.RequestPhotoCompletedDTO;
import john.api1.application.dto.mapper.request.search.RequestSearchDTO;
import john.api1.application.dto.mapper.request.search.RequestVideoCompletedDTO;
import john.api1.application.ports.repositories.request.IRequestCompletedSearchRepository;
import john.api1.application.ports.repositories.request.IRequestSearchRepository;
import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;
import john.api1.application.ports.services.IPetOwnerSearch;
import john.api1.application.ports.services.media.IMediaSearch;
import john.api1.application.ports.services.pet.IPetSearch;
import john.api1.application.ports.services.request.IRequestSearchAdmin;
import john.api1.application.services.history.HistoryLogSearchAS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class RequestSearchAdminAS implements IRequestSearchAdmin {
    private static final Logger log = LoggerFactory.getLogger(HistoryLogSearchAS.class);
    private final IRequestSearchRepository requestSearch;
    private final IRequestCompletedSearchRepository serviceRequestSearch;
    private final IPetOwnerSearch ownerSearch;
    private final IPetSearch petSearch;
    private final IMediaSearch mediaSearch;

    @Autowired
    public RequestSearchAdminAS(IRequestSearchRepository requestSearch,
                                IRequestCompletedSearchRepository serviceRequestSearch,
                                IPetOwnerSearch ownerSearch,
                                IPetSearch petSearch,
                                IMediaSearch mediaSearch) {
        this.requestSearch = requestSearch;
        this.serviceRequestSearch = serviceRequestSearch;
        this.ownerSearch = ownerSearch;
        this.petSearch = petSearch;
        this.mediaSearch = mediaSearch;
    }

    @Override
    public DomainResponse<List<RequestSearchDTO>> searchAllInProgress() {
        try {
            log.info("=======SEARCH-ALL-IN-PROGRESS REQUEST STARTS=======");
            var request = requestSearch.findAllByStatus(RequestStatus.IN_PROGRESS);
            if (request.isEmpty()) {
                log.warn("=======NO IN-PROGRESS REQUEST FOUND. RETURN EMPTY LIST=======");
                return DomainResponse.success(new ArrayList<>(), "No in-progress request founded");
            }

            var ok = loop(request, RequestStatus.IN_PROGRESS);
            return DomainResponse.success(ok, "Successfully retrieved " + ok.size() + " in-progress request");
        } catch (Exception e) {
            return DomainResponse.success(new ArrayList<>(), "No in-progress request. Note: Something wrong with the system.");
        }
    }

    @Override
    public DomainResponse<List<RequestSearchDTO>> searchAllPending() {
        try {

            log.info("=======SEARCH-ALL-PENDING REQUEST STARTS=======");
            var request = requestSearch.findAllByStatus(RequestStatus.PENDING);
            if (request.isEmpty()) {
                log.warn("=======NO PENDING REQUEST FOUND. RETURN EMPTY LIST=======");
                return DomainResponse.success(new ArrayList<>(), "No pending request founded");
            }
            var ok = loop(request, RequestStatus.PENDING);
            return DomainResponse.success(ok, "Successfully retrieved " + ok.size() + " pending request");

        } catch (Exception e) {
            return DomainResponse.success(new ArrayList<>(), "No pending request. Note: Something wrong with the system.");
        }
    }

    @Override
    public DomainResponse<List<RequestSearchDTO>> searchAllCompleted() {
        try {
            log.info("=======SEARCH-ALL-COMPLETED REQUEST STARTS=======");
            var request = requestSearch.findAllByStatus(RequestStatus.COMPLETED);
            if (request.isEmpty()) {
                log.warn("=======NO COMPLETED REQUEST FOUND. RETURN EMPTY LIST=======");
                return DomainResponse.success(new ArrayList<>(), "No completed request founded");
            }
            var ok = loop(request, RequestStatus.COMPLETED);
            return DomainResponse.success(ok, "Successfully retrieved " + ok.size() + " completed request");

        } catch (Exception e) {
            return DomainResponse.success(new ArrayList<>(), "No completed request. Note: Something wrong with the system.");
        }
    }

    @Override
    public DomainResponse<List<RequestSearchDTO>> searchAllRejected() {
        try {
            log.info("=======SEARCH-ALL-REJECTED REQUEST STARTS=======");
            var request = requestSearch.findAllByStatus(RequestStatus.REJECTED);
            if (request.isEmpty()) {
                log.warn("=======NO REJECTED REQUEST FOUND. RETURN EMPTY LIST=======");
                return DomainResponse.success(new ArrayList<>(), "No rejected request founded");
            }

            var ok = loop(request, RequestStatus.REJECTED);
            return DomainResponse.success(ok, "Successfully retrieved " + ok.size() + " rejected request");

        } catch (Exception e) {
            return DomainResponse.success(new ArrayList<>(), "No rejected request. Note: Something wrong with the system.");
        }
    }

    @Override
    public DomainResponse<List<RequestSearchDTO>> searchAllByOwnerId(String id) {
        try {
            log.info("=======SEARCH-ALL-FOR-PET-OWNER STARTS=======");
            var request = requestSearch.findAllByOwnerId(id);
            if (request.isEmpty()) {
                log.warn("=======NO SEARCH-ALL REQUEST FOUND. RETURN EMPTY LIST=======");
                return DomainResponse.success(new ArrayList<>(), "No rejected request founded");
            }

            var ok = loop(request, RequestStatus.REJECTED);
            return DomainResponse.success(ok, "Successfully retrieved " + ok.size() + " rejected request");

        } catch (Exception e) {
            return DomainResponse.success(new ArrayList<>(), "No rejected request. Note: Something wrong with the system.");
        }
    }


    private List<RequestSearchDTO> loop(List<RequestDomain> request, RequestStatus status) {
        return request.stream()
                .map(domain -> {
                    try {
                        return switch (domain.getRequestType()) {
                            case BOARDING_EXTENSION -> mapExtension(domain);
                            case GROOMING_SERVICE -> mapGrooming(domain);
                            case PHOTO_REQUEST -> {
                                if (status == RequestStatus.COMPLETED) yield mapPhoto(domain);
                                else yield mapMedia(domain);
                            }
                            case VIDEO_REQUEST -> {
                                if (status == RequestStatus.COMPLETED) yield mapVideo(domain);
                                else yield mapMedia(domain);
                            }
                        };
                    } catch (Exception e) {
                        log.error("FAILED MAPPING: Could not map request ID {}. Reason: {}", domain.getId(), e.getMessage());
                        return null; // ensures this one is skipped
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }


    private RequestSearchDTO mapExtension(RequestDomain domain) {
        try {
            log.info("EXTENSION REQUEST: Start data querying for request extension. Request-ID:{} | Owner-ID:{}, Pet-ID:{}", domain.getId(), domain.getOwnerId(), domain.getPetId());

            String ownerName = ownerSearch.getPetOwnerName(domain.getOwnerId());
            String petName = petSearch.getPetName(domain.getPetId());
            var extension = serviceRequestSearch.getExtensionByRequestIdCqrs(domain.getId());
            return extension.map(extensionCQRS ->
                            RequestExtensionCreatedDTO.searchMap(domain, extensionCQRS, ownerName, petName))
                    .orElse(null);

        } catch (PersistenceException | NullPointerException e) {
            log.error("EXTENSION REQUEST: Failed to query necessary data. Exception:{}", e.getMessage());
            return null;
        }
    }

    private RequestSearchDTO mapGrooming(RequestDomain domain) {
        try {
            log.info("GROOMING REQUEST: Start data querying for request grooming. Request-ID:{} | Owner-ID:{}, Pet-ID:{}", domain.getId(), domain.getOwnerId(), domain.getPetId());

            String ownerName = ownerSearch.getPetOwnerName(domain.getOwnerId());
            var petName = petSearch.getPetNameBreedSize(domain.getPetId());
            var grooming = serviceRequestSearch.getGroomingByRequestIdCqrs(domain.getId());
            return grooming.map(e ->
                            RequestGroomingCreatedDTO.map(domain, e, ownerName, petName.petName(), petName.size()))
                    .orElse(null);

        } catch (PersistenceException | NullPointerException e) {
            log.error("GROOMING REQUEST: Failed to query necessary data. Exception:{}", e.getMessage());
            return null;
        }
    }

    private RequestSearchDTO mapMedia(RequestDomain domain) {
        try {
            log.info("MEDIA REQUEST: Start data querying for request media. Request-ID:{} | Owner-ID:{}, Pet-ID:{}", domain.getId(), domain.getOwnerId(), domain.getPetId());

            String ownerName = ownerSearch.getPetOwnerName(domain.getOwnerId());
            String petName = petSearch.getPetName(domain.getPetId());

            return RequestMediaCreatedDTO.map(domain, ownerName, petName);
        } catch (PersistenceException | NullPointerException e) {
            log.error("MEDIA REQUEST: Failed to query necessary data. Exception:{}", e.getMessage());
            return null;
        }
    }

    private RequestSearchDTO mapPhoto(RequestDomain domain) {
        try {
            log.info("PHOTO REQUEST: Start data querying. Request-ID:{} | Owner-ID:{}, Pet-ID:{}",
                    domain.getId(), domain.getOwnerId(), domain.getPetId());

            String ownerName = ownerSearch.getPetOwnerName(domain.getOwnerId());
            String petName = petSearch.getPetName(domain.getPetId());
            String createAt = DateUtils.formatInstantWithTime(domain.getRequestTime());
            String completed = DateUtils.formatInstantWithTime(domain.getResolvedTime());

            var photoRequestOpt = serviceRequestSearch.findPhotoRequestByRequestId(domain.getId());
            if (photoRequestOpt.isEmpty()) {
                log.warn("PHOTO REQUEST: No photo request found. Request-ID: {}", domain.getId());
                return null;
            }

            var photoRequest = photoRequestOpt.get();
            var photos = photoRequest.photo();
            if (photos == null || photos.isEmpty()) {
                log.warn("PHOTO REQUEST: No photo entries present. Request-ID: {}", domain.getId());
                return null;
            }

            var list = new MediaIdUrlExpire[photos.size()];
            for (int i = 0; i < photos.size(); i++) {
                var mediaId = photos.get(i).id();
                var mediaOpt = mediaSearch.optionalFindById(mediaId);
                if (mediaOpt.isEmpty()) {
                    log.warn("PHOTO REQUEST: Media not found. Media-ID: {} | Request-ID: {}", mediaId, domain.getId());
                    return null;
                }
                list[i] = mediaOpt.get();
            }

            return RequestPhotoCompletedDTO.mapCompleted(
                    photoRequest, Arrays.asList(list), domain,
                    ownerName, petName, createAt, completed);

        } catch (Exception e) {
            log.error("PHOTO REQUEST: Unexpected failure. Request-ID:{} | Exception: {}", domain.getId(), e.getMessage(), e);
            return null;
        }
    }

    private RequestSearchDTO mapVideo(RequestDomain domain) {
        try {
            log.info("VIDEO REQUEST: Start data querying. Request-ID:{} | Owner-ID:{}, Pet-ID:{}",
                    domain.getId(), domain.getOwnerId(), domain.getPetId());

            String ownerName = ownerSearch.getPetOwnerName(domain.getOwnerId());
            String petName = petSearch.getPetName(domain.getPetId());
            String createAt = DateUtils.formatInstantWithTime(domain.getRequestTime());
            String completed = DateUtils.formatInstantWithTime(domain.getResolvedTime());

            var videoRequestOpt = serviceRequestSearch.findVideoRequestByRequestId(domain.getId());
            if (videoRequestOpt.isEmpty()) {
                log.warn("VIDEO REQUEST: No video request found. Request-ID: {}", domain.getId());
                return null;
            }

            // SEARCH VIDEO
            var videoRequest = videoRequestOpt.get();
            var mediaId = videoRequest.mediaId();
            var mediaOpt = mediaSearch.optionalFindById(mediaId);

            if (mediaOpt.isEmpty()) {
                log.warn("VIDEO REQUEST: Media not found. Media-ID: {} | Request-ID: {}", mediaId, domain.getId());
                return null;
            }

            return RequestVideoCompletedDTO.mapCompleted(
                    videoRequest, mediaOpt.get(), domain,
                    ownerName, petName, createAt, completed);

        } catch (
                Exception e) {
            log.error("VIDEO REQUEST: Unexpected failure. Request-ID:{} | Exception: {}", domain.getId(), e.getMessage(), e);
            return null;
        }
    }

}
