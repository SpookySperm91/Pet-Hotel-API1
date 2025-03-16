package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.PetRegisterResponseDTO;
import john.api1.application.dto.request.PetRequestDTO;
import john.api1.application.ports.services.IPetRegister;
import john.api1.application.ports.services.IPetUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/pets/")
public class AdminPetController {
    private final IPetRegister petRegister;
    private final IPetUpdate petUpdate;

    @Autowired
    public AdminPetController(IPetRegister petRegister, IPetUpdate petUpdate) {
        this.petRegister = petRegister;
        this.petUpdate = petUpdate;
    }

    @PostMapping("register")
    public ResponseEntity<DTOResponse<PetRegisterResponseDTO>> registerPet(
            @Valid @RequestBody PetRequestDTO petRequest,
            BindingResult result) {

        if (result.hasErrors()) {
            result.getFieldError();
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError().getDefaultMessage());
        }


        var registerPet = petRegister.registerPet(petRequest);
        if (!registerPet.isSuccess()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    registerPet.getMessage()
            );
        }

        // api for uploading profile picture
        String api = registerPet.getData() + "/upload-photo?token=";
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        new PetRegisterResponseDTO(
                                registerPet.getData(),
                                api),
                        registerPet.getMessage()
                ));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(DTOResponse.of(status.value(), data, message));
    }
}
