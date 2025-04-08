package john.api1.application.services.request;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.dto.request.request.RequestExtensionRDTO;
import john.api1.application.dto.request.request.RequestGroomingRDTO;
import john.api1.application.dto.request.request.RequestMediaRDTO;
import john.api1.application.ports.services.media.IMediaManagement;
import john.api1.application.ports.services.request.IRequestCreate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestCreateAS implements IRequestCreate {
    private final IMediaManagement mediaManagement;

    @Autowired
    public RequestCreateAS(IMediaManagement mediaManagement) {
        this.mediaManagement = mediaManagement;
    }

    @Override
    public DomainResponse<String> createRequestMedia(RequestMediaRDTO request) {
        try {
            validateId(request.getOwnerId(), request.getPetId());


            return DomainResponse.success();
        } catch (PersistenceException | DomainArgumentException e) {
            return DomainResponse.error(e.getMessage());
        }
    }


    // Later
    @Override
    public DomainResponse<String> createRequestExtension(RequestExtensionRDTO request) {
        validateId(request.getOwnerId(), request.getPetId());
        return DomainResponse.success();
    }


    @Override
    public DomainResponse<String> createRequestGrooming(RequestGroomingRDTO request) {
        return DomainResponse.success();
    }


    private void validateId(String ownerId, String petId) {
        if (!ObjectId.isValid(ownerId))
            throw new PersistenceException("Owner id cannot be converted into ObjectId");
        if (!ObjectId.isValid(petId))
            throw new PersistenceException("Pet id cannot be converted into ObjectId");
    }
}
