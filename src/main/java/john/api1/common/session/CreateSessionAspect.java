package john.api1.common.session;

import jakarta.servlet.http.HttpServletResponse;
import john.api1.application.dto.DTOResponse;
import john.api1.application.ports.services.ISessionService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CreateSessionAspect {
    private final Logger logger = LoggerFactory.getLogger(CreateSessionAspect.class);
    private final ISessionService sessionService;
    private final HttpServletResponse httpResponse;

    @Autowired
    public CreateSessionAspect(ISessionService sessionService, HttpServletResponse httpResponse) {
        this.sessionService = sessionService;
        this.httpResponse = httpResponse;
    }

    @AfterReturning(pointcut = "@annotation(createSession)", returning = "responseEntity")
    public void handleSessionCreation(Object responseEntity, CreateSession createSession) {
        logger.info("🔍 Aspect triggered for role: {}", createSession.role()); // Log the role

        if (responseEntity instanceof ResponseEntity<?>) {
            ResponseEntity<?> entity = (ResponseEntity<?>) responseEntity;
            Object body = entity.getBody();

            // Handle methods returning DTOResponse
            if (body instanceof DTOResponse<?>) {
                handleDTOResponse((DTOResponse<?>) body, createSession.role());
            }
            // Handle methods returning non-DTO response types (e.g., String, Integer)
            else {
                handleNonDTOResponse(body, createSession.role());
            }
        } else {
            logger.warn("Response is not a ResponseEntity. Skipping session creation.");
        }
    }

    // Handle DTOResponse
    private void handleDTOResponse(DTOResponse<?> responseBody, SessionRole role) {
        if (responseBody.getStatus() >= 200 && responseBody.getStatus() < 300) {
            createSessionToken(role);
        } else {
            logger.warn("Session creation skipped. HTTP Status: {} does not meet the success criteria (200-299).", responseBody.getStatus());
        }
    }

    // Handle non-DTO response types
    private void handleNonDTOResponse(Object body, SessionRole role) {
        // For example, String or Integer responses
        if (body instanceof String || body instanceof Integer) {
            logger.info("Response body is a {}. Skipping session creation.", body.getClass().getSimpleName());
        } else {
            logger.warn("Response body is of type {}. Skipping session creation.", body.getClass().getSimpleName());
        }
    }

    // Session creation logic (for both cases)
    private void createSessionToken(SessionRole role) {
        try {
            String token = sessionService.generateSessionToken();  // Generate a token
            sessionService.saveSession(token, role);  // Save session
            httpResponse.setHeader("Session-Token", token);  // Send the token back in the response header
            logger.info("Session created successfully for role: {} with token: {}", role, token);
        } catch (Exception e) {
            logger.error("Failed to create session for role: {}", role, e);
        }
    }
}


