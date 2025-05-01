package john.api1.application.adapters.controllers.user;

import john.api1.application.components.enums.AccountCredentialType;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.request.LoginEmailRDTO;
import john.api1.application.dto.request.LoginPhoneNumberRDTO;
import john.api1.application.ports.services.ILoginPetOwner;
import john.api1.common.session.CreateSession;
import john.api1.common.session.InvalidateSession;
import john.api1.common.session.SessionRole;
import john.api1.common.session.ValidateSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pet-owner/")
public class PetOwnerLoginController {
    private final ILoginPetOwner loginPetOwner;

    @Autowired
    public PetOwnerLoginController(ILoginPetOwner loginPetOwner) {
        this.loginPetOwner = loginPetOwner;
    }

    @PostMapping("/login/email")
    @CreateSession(role = SessionRole.PET_OWNER)
    public ResponseEntity<DTOResponse<String>> loginWithEmail(
            @Valid @RequestBody LoginEmailRDTO request,
            BindingResult result) {

        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var response = loginPetOwner.login(
                AccountCredentialType.EMAIL,
                request.getEmail(),
                request.getPassword());

        return response.isSuccess()
                ? ResponseEntity.ok(DTOResponse.of(
                HttpStatus.OK.value(),
                response.getData(),
                response.getMessage()))
                : buildErrorResponse(HttpStatus.BAD_REQUEST, response.getMessage());
    }

    @PostMapping("/login/phone-number")
    @CreateSession(role = SessionRole.PET_OWNER)
    public ResponseEntity<DTOResponse<String>> loginWithPhone(
            @Valid @RequestBody LoginPhoneNumberRDTO request,
            BindingResult result) {

        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var response = loginPetOwner.login(
                AccountCredentialType.PHONE_NUMBER,
                request.getPhoneNumber(),
                request.getPassword());

        return response.isSuccess()
                ? ResponseEntity.ok(DTOResponse.of(
                HttpStatus.OK.value(),
                response.getData(),
                response.getMessage()))
                : buildErrorResponse(HttpStatus.BAD_REQUEST, response.getMessage());
    }

    @PostMapping("/logout")
    @ValidateSession(role = SessionRole.PET_OWNER)
    public ResponseEntity<String> logout(@RequestParam String id) {
        var account = loginPetOwner.logout(id);
        return account.isSuccess()
                ? ResponseEntity.status(HttpStatus.OK).body(account.getMessage())
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(account.getMessage());
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
