package john.api1.application.adapters.controllers.admin;

import john.api1.application.components.ExternalReferencesUrls;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.dto.mapper.RegisterDTO;
import john.api1.application.dto.request.EmailRDTO;
import john.api1.application.dto.request.NewPasswordRDTO;
import john.api1.application.dto.request.RegisterRDTO;
import john.api1.application.dto.request.admin.AdminLoginRDTO;
import john.api1.application.ports.services.IRegisterClientApprove;
import john.api1.application.ports.services.IRegisterNewClient;
import john.api1.application.ports.services.admin.IAdminLogin;
import john.api1.application.services.response.RegisterResponse;
import john.api1.common.session.CreateSession;
import john.api1.common.session.InvalidateSession;
import john.api1.common.session.SessionRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final IRegisterNewClient<RegisterRDTO, RegisterResponse> register;
    private final IRegisterClientApprove approve;
    private final IAdminLogin adminLogin;
    private final ExternalReferencesUrls referencesUrls;


    @Autowired
    public AdminController(@Qualifier("RegisterNewClientAS") IRegisterNewClient<RegisterRDTO, RegisterResponse> register,
                           IRegisterClientApprove approve,
                           IAdminLogin adminLogin,
                           ExternalReferencesUrls referencesUrls) {
        this.register = register;
        this.approve = approve;
        this.adminLogin = adminLogin;
        this.referencesUrls = referencesUrls;
    }

    @PostMapping("/register/pet-owner")
    public ResponseEntity<DTOResponse<RegisterDTO>> registerPetOwner(
            @Valid @RequestBody RegisterRDTO request) {
        // Call registration service
        var response = register.registerNewClient(request);

        if (response.isSuccess()) {
            RegisterDTO dto = response.getData().toDTO();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DTOResponse.of(
                            HttpStatus.CREATED.value(),
                            dto,
                            response.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(DTOResponse.message(HttpStatus.BAD_REQUEST.value(), response.getMessage()));
    }

    @PutMapping("/register/pet-owner/{id}/approve")
    public ResponseEntity<DTOResponse<Void>> approvePendingOwnerAccount(@PathVariable String id) {
        var apv = approve.approvePendingPetOwnerAccount(id);

        if (!apv.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, apv.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(),apv.getMessage()));
    }


    // login
    @PostMapping("/account/login")
    @CreateSession(role = SessionRole.ADMIN)
    public ResponseEntity<DTOResponse<AdminDTO>> adminLogin(
            @Valid @RequestBody AdminLoginRDTO request,
            BindingResult result) {

        if (result.hasErrors())
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError().getDefaultMessage());


        var login = adminLogin.login(request);
        if (!login.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, login.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), login.getData(), login.getMessage()));
    }

    // logout
    @PostMapping("/account/{id}/logout")
    @InvalidateSession()
    public ResponseEntity<DTOResponse<Void>> adminLogout(@PathVariable String id) {

        var login = adminLogin.logout(id);
        if (!login.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, login.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), login.getMessage()));
    }

    // request reset password
    @PostMapping("/account/reset-password/request")
    public ResponseEntity<DTOResponse<Void>> adminRequestResetPassword(
            @Valid @RequestBody EmailRDTO request,
            BindingResult result) {

        if (result.hasErrors())
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError().getDefaultMessage());


        var resetPassword = adminLogin.requestPasswordReset(request.getEmail());
        if (!resetPassword.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, resetPassword.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), resetPassword.getMessage()));
    }

    // verify link
    @PostMapping("/account/reset-password/{id}/verify/{token}")
    public ResponseEntity<DTOResponse<String>> adminVerifyLink(
            @PathVariable String id,
            @PathVariable String token) {


        var resetPassword = adminLogin.verifyPasswordLink(id, token);
        if (!resetPassword.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, resetPassword.getMessage());

        String resetApi = String.format("%s/api/v1/admin/account/reset-password/%s/change-password/%s",
                referencesUrls.getSpringbootApiUrl(), id, token);

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), resetApi, resetPassword.getMessage()));
    }

    // change password
    @PostMapping("/account/reset-password/{id}/change-password/{token}")
    public ResponseEntity<DTOResponse<Void>> changePassword(
            @PathVariable String id,
            @PathVariable String token,
            @Valid @RequestBody NewPasswordRDTO newPassword,
            BindingResult result) {

        if (result.hasErrors())
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    result.getFieldError().getDefaultMessage());

        var reset = adminLogin.changePassword(token, id, newPassword.getNewPassword());
        if (!reset.isSuccess())
            return buildErrorResponse(HttpStatus.BAD_REQUEST, reset.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), reset.getMessage()));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}

