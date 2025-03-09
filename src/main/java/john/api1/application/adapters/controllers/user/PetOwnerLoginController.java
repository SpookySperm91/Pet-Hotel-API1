package john.api1.application.adapters.controllers.user;

import john.api1.application.components.enums.AccountCredentialType;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.LoginResponseDTO;
import john.api1.application.dto.request.LoginEmailRequestDTO;
import john.api1.application.dto.request.LoginPhoneNumberRequestDTO;
import john.api1.application.ports.services.ILoginPetOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/pet-owner/")
public class PetOwnerLoginController {
    private final ILoginPetOwner loginPetOwner;

    @Autowired
    public PetOwnerLoginController(ILoginPetOwner loginPetOwner) {
        this.loginPetOwner = loginPetOwner;
    }

    @PostMapping("/login/email")
    public ResponseEntity<DTOResponse<LoginResponseDTO>> loginWithEmail(
            @Valid @RequestBody LoginEmailRequestDTO request,
            BindingResult result) {
        return validateAndLogin(
                result,
                AccountCredentialType.EMAIL,
                request.getEmail(),
                request.getPassword(),
                "email");
    }

    @PostMapping("/login/phone-number")
    public ResponseEntity<DTOResponse<LoginResponseDTO>> loginWithPhone(
            @Valid @RequestBody LoginPhoneNumberRequestDTO request,
            BindingResult result) {
        return validateAndLogin(
                result,
                AccountCredentialType.PHONE_NUMBER,
                request.getPhoneNumber(),
                request.getPassword(),
                "phoneNumber");
    }

    // Login
    private ResponseEntity<DTOResponse<LoginResponseDTO>> validateAndLogin(
            BindingResult result,
            AccountCredentialType type,
            String userAccount,
            String password,
            String field) {

        if (result.hasErrors()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError(field).getDefaultMessage());
        }

        var response = loginPetOwner.login(type, userAccount, password);
        return response.isSuccess()
                // success
                ? ResponseEntity.ok(
                DTOResponse.of(
                        HttpStatus.OK.value(),
                        new LoginResponseDTO(response.getData()),
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
