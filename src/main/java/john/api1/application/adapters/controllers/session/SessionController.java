package john.api1.application.adapters.controllers.session;

import john.api1.common.session.SessionRole;
import john.api1.common.session.ValidateSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session/valid")
public class SessionController {

    @ValidateSession(role = SessionRole.PET_OWNER)
    @GetMapping("/admin")
    public ResponseEntity<Void> validatePetOwnerSession() {
            return ResponseEntity.ok().build();
    }

    @ValidateSession(role = SessionRole.ADMIN)
    @GetMapping("/pet-owner")
    public ResponseEntity<Void> validateAdminSession() {
            return ResponseEntity.ok().build();
    }
}
