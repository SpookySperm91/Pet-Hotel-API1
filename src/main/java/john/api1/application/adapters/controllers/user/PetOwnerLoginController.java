package john.api1.application.adapters.controllers.user;

import john.api1.application.components.enums.AccountCredentialType;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.request.LoginEmailRequestDTO;
import john.api1.application.dto.request.LoginPhoneNumberRequestDTO;
import john.api1.application.ports.services.ILoginPetOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
    public ResponseEntity<DTOResponse<String>> loginWithEmail(
            @Valid @RequestBody LoginEmailRequestDTO request,
            BindingResult result) {
        return validateAndLogin(
                result,
                AccountCredentialType.EMAIL,
                request.getEmail(),
                request.getPassword());
    }

    @PostMapping("/login/phone-number")
    public ResponseEntity<DTOResponse<String>> loginWithPhone(
            @Valid @RequestBody LoginPhoneNumberRequestDTO request,
            BindingResult result) {
        return validateAndLogin(
                result,
                AccountCredentialType.PHONE_NUMBER,
                request.getPhoneNumber(),
                request.getPassword());
    }

    // Login
    private ResponseEntity<DTOResponse<String>> validateAndLogin(
            BindingResult result,
            AccountCredentialType type,
            String userAccount,
            String password) {

        if (result.hasErrors()) {
            // Collect all validation errors
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            // Join messages into a single string or return as a list
            String combinedErrors = String.join(", ", errorMessages);
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var response = loginPetOwner.login(type, userAccount, password);
        return response.isSuccess()
                // success
                ? ResponseEntity.ok(
                DTOResponse.of(
                        HttpStatus.OK.value(),
                        response.getData(),
                        response.getMessage()))
                // error
                : buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                response.getMessage());
    }


    @PostMapping("/logout")
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
