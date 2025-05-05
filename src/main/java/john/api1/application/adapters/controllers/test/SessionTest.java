package john.api1.application.adapters.controllers.test;

import john.api1.application.dto.DTOResponse;
import john.api1.common.session.CreateSession;
import john.api1.common.session.InvalidateSession;
import john.api1.common.session.SessionRole;
import john.api1.common.session.ValidateSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/session/test/")
public class SessionTest {


    @GetMapping("/test")
    @CreateSession(role = SessionRole.PET_OWNER)
    public ResponseEntity<DTOResponse<Void>> test() {
        return ResponseEntity.ok(DTOResponse.of(200));
    }

    @GetMapping("/test2")
    @ValidateSession(role = SessionRole.ADMIN)
    public ResponseEntity<DTOResponse<Void>> test2() {
        return ResponseEntity.ok(DTOResponse.of(200, "session works"));
    }

    @GetMapping("/test3")
    @InvalidateSession
    public ResponseEntity<DTOResponse<Void>> test3() {
        return ResponseEntity.ok(DTOResponse.of(200, "session works"));
    }
}
