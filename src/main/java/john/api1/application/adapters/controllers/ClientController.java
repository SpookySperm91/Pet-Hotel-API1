package john.api1.application.adapters.controllers;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.RegisterRequestDTO;
import john.api1.application.services.admin.RegisterNewClientAS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/pet-owner")
public class ClientController {
    private final RegisterNewClientAS register;

    @Autowired
    public ClientController(RegisterNewClientAS register) {
        this.register = register;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPetOwner(@Valid @RequestBody RegisterRequestDTO request) {
        DomainResponse<String> response = register.registerNewClient(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Registration successful", "clientId", response.getDomainObject()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", response.getMessage()));
        }
    }
}

