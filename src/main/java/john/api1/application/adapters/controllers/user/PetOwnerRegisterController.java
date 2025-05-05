package john.api1.application.adapters.controllers.user;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.request.RegisterOwnerRDTO;
import john.api1.application.ports.services.IRegisterNewClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pet-owner/")
public class PetOwnerRegisterController {
    private final IRegisterNewClient<RegisterOwnerRDTO, String> register;

    @Autowired
    public PetOwnerRegisterController(@Qualifier("RegisterOwnerAS") IRegisterNewClient<RegisterOwnerRDTO, String> register) {
        this.register = register;
    }

    @PostMapping("register")
    public ResponseEntity<DTOResponse<String>> register(
            @Valid @RequestBody RegisterOwnerRDTO request,
            BindingResult result) {

        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var reg = register.registerNewClient(request);
        if (!reg.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, reg.getMessage());

        return ResponseEntity.status(HttpStatus.OK).body(
                DTOResponse.of(HttpStatus.OK.value(), reg.getData(), reg.getMessage())
        );
    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
