package john.api1.application.dto.mapper.interfaces;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.mapper.RegisterResponseDTO;
import john.api1.application.services.response.RegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;

@Mapper
public interface RegisterMapper {
    RegisterMapper INSTANCE = Mappers.getMapper(RegisterMapper.class);
    @Mapping(source = "message", target = "message")
    @Mapping(source = "domainObject", target = "data")
    @Mapping(target = "requestAt", expression = "java(java.time.Instant.now())")
    RegisterResponseDTO toDTO(DomainResponse<RegisterResponse> response);
}

