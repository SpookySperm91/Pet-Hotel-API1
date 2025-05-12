package john.api1.application.ports.services;

import john.api1.application.domain.models.request.ExtensionDomain;
import john.api1.application.domain.models.request.GroomingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.ports.repositories.request.GroomingCQRS;

public interface IRequestAggregation {
    RequestMediaCreatedDTO requestCreateMediaAggregation(RequestDomain domain, String ownerName, String petName);

    RequestExtensionCreatedDTO requestCreateExtensionAggregation(RequestDomain domain, ExtensionDomain extension, String ownerName, String petName, String unit);

    RequestGroomingCreatedDTO requestCreateGroomingAggregation(RequestDomain domain, GroomingCQRS grooming, String ownerName, String petName, String petSize);

    RequestGroomingCreatedDTO requestCreateGroomingAggregation(RequestDomain domain, GroomingDomain grooming, String ownerName, String petName, String petSize);
}