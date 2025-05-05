package john.api1.application.adapters.controllers.master;

import jakarta.validation.Valid;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.AdminDTO;
import john.api1.application.dto.request.admin.AdminChangeRDTO;
import john.api1.application.dto.request.admin.AdminCreateRDTO;
import john.api1.application.ports.services.admin.IAdminCreate;
import john.api1.application.ports.services.admin.IAdminManage;
import john.api1.application.ports.services.admin.IAdminSearch;
import john.api1.common.session.SessionRole;
import john.api1.common.session.ValidateSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/master/")
public class MasterController {
    private final IAdminSearch adminSearch;
    private final IAdminCreate adminCreate;
    private final IAdminManage adminManage;

    @Autowired
    public MasterController(IAdminSearch adminSearch,
                            IAdminCreate adminCreate,
                            IAdminManage adminManage) {
        this.adminSearch = adminSearch;
        this.adminCreate = adminCreate;
        this.adminManage = adminManage;

    }

    @PostMapping("/create/admin-account")
    @ValidateSession(role = SessionRole.MASTER)
    public ResponseEntity<DTOResponse<AdminDTO>> registerNewAdmin(
            @Valid @RequestBody AdminCreateRDTO request,
            BindingResult result) {

        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var re = adminCreate.registerNewAdmin(request);
        if (!re.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, re.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        re.getData(),
                        re.getMessage()));
    }

    @PutMapping("/update/password")
    @ValidateSession(role = SessionRole.MASTER)
    public ResponseEntity<DTOResponse<Void>> changePassword(
            @Valid @RequestBody AdminChangeRDTO request,
            BindingResult result) {
        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", password"));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var pas = adminManage.updatePassword(request.getId(), request.getData());
        if (!pas.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, pas.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        pas.getMessage()));
    }

    @PutMapping("/update/username")
    @ValidateSession(role = SessionRole.MASTER)
    public ResponseEntity<DTOResponse<Void>> changeUsername(
            @Valid @RequestBody AdminChangeRDTO request,
            BindingResult result) {
        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", username "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var urs = adminManage.updateUsername(request.getId(), request.getData());
        if (!urs.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, urs.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        urs.getMessage()));
    }

    @PutMapping("/update/email")
    @ValidateSession(role = SessionRole.MASTER)
    public ResponseEntity<DTOResponse<Void>> changeEmail(
            @Valid @RequestBody AdminChangeRDTO request,
            BindingResult result) {
        if (result.hasErrors()) {
            String combinedErrors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", email "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, combinedErrors);
        }

        var eml = adminManage.updateEmail(request.getId(), request.getData());
        if (!eml.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, eml.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        eml.getMessage()));
    }

    @PutMapping("/delete/{adminId}")
    @ValidateSession(role = SessionRole.MASTER)
    public ResponseEntity<DTOResponse<Void>> changeUsername(
            @Valid @PathVariable String adminId) {
        if (adminId == null || adminId.isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Admin id cannot be empty");
        }

        var del = adminManage.deleteAdminById(adminId);
        if (!del.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, del.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        del.getMessage()));
    }


    @PutMapping("/delete/inactive-all")
    @ValidateSession(role = SessionRole.MASTER)
    public ResponseEntity<DTOResponse<Void>> deleteAllInactive() {
        var del = adminManage.deleteInactiveAdmin();

        if (!del.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, del.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        del.getMessage()));
    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}
