package john.api1.application.services.aggregation;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.ports.repositories.request.GroomingCQRS;
import john.api1.application.ports.services.IRequestAggregation;
import org.springframework.stereotype.Component;

@Component
public class AggregationRequest implements IRequestAggregation {

    public RequestMediaCreatedDTO requestCreateMediaAggregation(RequestDomain domain, String ownerName, String petName) {
        return RequestMediaCreatedDTO.map(domain, ownerName, petName);
    }

    public RequestExtensionCreatedDTO requestCreateExtensionAggregation(RequestDomain domain, ExtensionDomain extension, String ownerName, String petName, String unit) {
        return RequestExtensionCreatedDTO.map(domain, extension, ownerName, petName, unit);
    }

    public RequestGroomingCreatedDTO requestCreateGroomingAggregation(RequestDomain domain, GroomingCQRS grooming, String ownerName, String petName, String petSize) {
        return RequestGroomingCreatedDTO.map(domain, grooming, ownerName, petName, petSize);
    }

    public RequestGroomingCreatedDTO requestCreateGroomingAggregation(RequestDomain domain, GroomingDomain grooming, String ownerName, String petName, String petSize) {
        return RequestGroomingCreatedDTO.map(domain, grooming, ownerName, petName, petSize);
    }
}
