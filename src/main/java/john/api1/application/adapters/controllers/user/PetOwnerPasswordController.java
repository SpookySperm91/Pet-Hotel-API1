package john.api1.application.adapters.controllers.user;

import john.api1.application.components.ExternalReferencesUrls;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.VerifyLinkResponseDTO;
import john.api1.application.dto.request.EmailRDTO;
import john.api1.application.dto.request.NewPasswordRDTO;
import john.api1.application.services.user.ResetPasswordAS;
import john.api1.application.services.user.ResetPasswordRequestAS;
import john.api1.application.services.user.ResetPasswordValidateAS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/reset-password/")
public class PetOwnerPasswordController {
    private final ResetPasswordRequestAS passwordRequest;
    private final ResetPasswordValidateAS passwordValidate;
    private final ResetPasswordAS resetPassword;
    private final ExternalReferencesUrls referencesUrls;


    @Autowired
    public PetOwnerPasswordController(ResetPasswordValidateAS passwordValidate,
                                      ResetPasswordRequestAS passwordRequest,
                                      ResetPasswordAS resetPassword,
                                      ExternalReferencesUrls referencesUrls) {
        this.passwordValidate = passwordValidate;
        this.passwordRequest = passwordRequest;
        this.resetPassword = resetPassword;
        this.referencesUrls = referencesUrls;
    }

    // RequestRDTO reset password link
    @PostMapping("request/verification-link")
    public ResponseEntity<DTOResponse<String>> requestResetLink(
            @Valid @RequestBody EmailRDTO email,
            BindingResult result) {

        if (result.hasErrors()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError().getDefaultMessage());
        }

        try {
            var request = passwordRequest.sendVerificationLink(email.getEmail());
            //
            // session and magics (later)
            //
            return ResponseEntity.status(HttpStatus.OK)
                    .body(DTOResponse.of(
                            HttpStatus.OK.value(),
                            "If the email exists in our system, a verification link has been sent. Please check your inbox."));
        } catch (Exception e) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Something went wrong. Please try again later.");
        }
    }

    // Validate link
    // Return VALID, INVALID, EXPIRED
    @GetMapping("verify/{id}/{token}")
    public ResponseEntity<DTOResponse<VerifyLinkResponseDTO>> validateResetLink(
            @PathVariable String id,
            @PathVariable String token) {

        var response = passwordValidate.checkLinkIfValid(id, token);
        if (!response.isSuccess()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    response.getMessage(),
                    new VerifyLinkResponseDTO(
                            response.getData().status().getResponseStatus().toUpperCase(),
                            null,
                            null));
        }

        // api link
        String resetApi = String.format("%sapi/v1/reset-password/confirm/%s/%s",
                referencesUrls.getSpringbootApiUrl(), id, token);

        // Return valid-status, username, api-to-confirm-password
        return ResponseEntity.status(HttpStatus.OK).body(
                DTOResponse.of(
                        HttpStatus.OK.value(),
                        new VerifyLinkResponseDTO(
                                response.getData().status().getResponseStatus().toUpperCase(),
                                response.getData().username(),
                                resetApi),
                        response.getMessage()));
    }

    // Validate password input
    // Validate id and token
    // Reset password
    @PostMapping("confirm/{id}/{token}")
    public ResponseEntity<DTOResponse<String>> changePassword(
            @PathVariable String id,
            @PathVariable String token,
            @Valid @RequestBody NewPasswordRDTO newPassword,
            BindingResult result) {

        // Validate password input
        if (result.hasErrors()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError().getDefaultMessage());
        }

        // Check id and token if valid
        var validationResponse = passwordValidate.checkLinkIfValid(id, token);
        if (!validationResponse.isSuccess()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    validationResponse.getMessage());
        }

        // Reset password
        var resetResponse = resetPassword.resetPassword(id, token, newPassword.getNewPassword());
        return resetResponse.isSuccess()
                // success
                ? ResponseEntity.ok(DTOResponse.of(
                HttpStatus.OK.value(),
                resetResponse.getData(),
                resetResponse.getMessage()))
                // error
                : buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                resetResponse.getMessage());
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(DTOResponse.of(status.value(), data, message));
    }
}
