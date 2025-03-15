package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.RegisterResponseDTO;
import john.api1.application.dto.request.RegisterRequestDTO;
import john.api1.application.ports.services.IRegisterNewClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/api/v1/admin/")
public class AdminController {
    private final IRegisterNewClient register;

    @Autowired
    public AdminController(IRegisterNewClient register) {
        this.register = register;
    }

    @PostMapping("/register")
    public ResponseEntity<DTOResponse<RegisterResponseDTO>> registerPetOwner(@Valid @RequestBody RegisterRequestDTO request) {
        // Call registration service
        var response = register.registerNewClient(request);

        if (response.isSuccess()) {
            RegisterResponseDTO dto = response.getData().toDTO();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DTOResponse.of(HttpStatus.CREATED.value(), dto, response.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(DTOResponse.message(HttpStatus.BAD_REQUEST.value(), response.getMessage()));
        }
    }
}

