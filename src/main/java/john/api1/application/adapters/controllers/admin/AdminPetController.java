package john.api1.application.adapters.controllers.admin;

import john.api1.application.components.ExternalReferencesUrls;
import john.api1.application.components.enums.EndpointType;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.PetRegisterDTO;
import john.api1.application.dto.mapper.ProfileDTO;
import john.api1.application.dto.request.PetRDTO;
import john.api1.application.ports.services.pet.IPetProfilePhoto;
import john.api1.application.ports.services.pet.IPetRegister;
import john.api1.application.services.TokenAS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/pets/")
public class AdminPetController {
    private final IPetRegister petRegister;
    private final IPetProfilePhoto petProfilePic;
    private final TokenAS tokenService;
    private final ExternalReferencesUrls referencesUrls;


    @Autowired
    public AdminPetController(IPetRegister petRegister, IPetProfilePhoto petProfilePic, TokenAS tokenService, ExternalReferencesUrls referencesUrls) {
        this.petRegister = petRegister;
        this.petProfilePic = petProfilePic;
        this.tokenService = tokenService;
        this.referencesUrls = referencesUrls;
    }

    @PostMapping("register")
    public ResponseEntity<DTOResponse<PetRegisterDTO>> registerPet(
            @Valid @RequestBody PetRDTO petRequest,
            BindingResult result) {

        // Handle validation errors properly
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }

        var registerResponse = petRegister.registerPet(petRequest);
        if (!registerResponse.isSuccess()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, registerResponse.getMessage()
            );
        }

        String petId = registerResponse.getData();

        // Generate token (auto save)
        var tokenResponse = tokenService.createToken(petId, EndpointType.UPLOAD_PET_PHOTO);
        if (!tokenResponse.isSuccess()) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token.");
        }

        // Format the API URL
        String requestUploadUrl = referencesUrls.getSpringbootApiUrl() + EndpointType.UPLOAD_PET_PHOTO.getFormattedEndpoint(
                petId, petRequest.getPetName(), tokenResponse.getData().getToken()
        );

        // api for uploading profile picture
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        new PetRegisterDTO(
                                tokenResponse.getData().getAuthorizedId(),
                                requestUploadUrl),
                        registerResponse.getMessage()
                ));
    }

    // Check URL if valid
    // Call service layer to process
    // Return response (url)
    @PostMapping("register/{id}/{petName}/upload-photo/{token}")
    public ResponseEntity<DTOResponse<ProfileDTO>> updatePetPhoto(
            @PathVariable String id,
            @PathVariable String petName,
            @PathVariable String token
    ) {
        // Step 1: Check if token is valid
        var checkToken = tokenService.checkTokenValid(token, id, EndpointType.UPLOAD_PET_PHOTO);
        if (!checkToken.isSuccess()) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, checkToken.getMessage());
        }

        // Step 2: Call service to process
        var processUrl = petProfilePic.processProfilePhoto(id, petName);

        if (!processUrl.isSuccess()) {
            HttpStatus status;
            switch (processUrl.getErrorType()) {
                case VALIDATION_ERROR -> status = HttpStatus.BAD_REQUEST;
                case NOT_FOUND -> status = HttpStatus.NOT_FOUND;
                default -> status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            return buildErrorResponse(status, processUrl.getMessage());
        }

        // Return response
        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        processUrl.getData()));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
