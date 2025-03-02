package john.api1.application.adapters.controllers;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.RegisterResponseDTO;
import john.api1.application.dto.mapper.interfaces.RegisterMapper;
import john.api1.application.dto.request.RegisterRequestDTO;
import john.api1.application.services.admin.RegisterNewClientAS;
import john.api1.application.services.response.RegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.Instant;

@Controller
@RequestMapping("/api/v1/admin/")
public class PetOwnerControllerAdmin {
    private final RegisterNewClientAS register;

    @Autowired
    public PetOwnerControllerAdmin(RegisterNewClientAS register) {
        this.register = register;
    }

    @PostMapping("/register")
    public ResponseEntity<DTOResponse<RegisterResponseDTO>> registerPetOwner(@Valid @RequestBody RegisterRequestDTO request) {
        // Call registration service
        DomainResponse<RegisterResponse> response = register.registerNewClient(request);

        if (response.isSuccess()) {
            HttpStatus status = HttpStatus.CREATED;
            RegisterResponseDTO dto = new RegisterResponseDTO(
                    response.getDomainObject());

            return ResponseEntity.status(status)
                    .body(DTOResponse.success(status.value(), dto));
        } else {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .body(DTOResponse.error(status.value(), response.getMessage()));
        }
    }
}

