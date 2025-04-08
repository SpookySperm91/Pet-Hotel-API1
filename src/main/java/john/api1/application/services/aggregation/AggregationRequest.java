package john.api1.application.services.aggregation;

import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.ports.services.IRequestAggregation;
import org.springframework.stereotype.Service;

@Service
public class AggregationRequest implements IRequestAggregation {

    public RequestMediaCreatedDTO requestCreateMediaAggregation(RequestDomain domain, String ownerName, String petName) {
        return RequestMediaCreatedDTO.map(domain, ownerName, petName);
    }

    public RequestExtensionCreatedDTO requestCreateExtensionAggregation(RequestDomain domain, String ownerName, String petName, long duration, String unit) {
        return RequestExtensionCreatedDTO.map(domain, ownerName, petName, duration, unit);
    }

    public RequestGroomingCreatedDTO requestCreateGroomingAggregation(RequestDomain domain, String ownerName, String petName, double price, String size) {
        return RequestGroomingCreatedDTO.map(domain, ownerName, petName, price, size);
    }
}
