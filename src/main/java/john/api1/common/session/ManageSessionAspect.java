package john.api1.common.session;

import jakarta.servlet.http.HttpServletRequest;
import john.api1.application.components.exception.SessionException;
import john.api1.application.ports.services.ISessionService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ManageSessionAspect {
    private final Logger logger = LoggerFactory.getLogger(ManageSessionAspect.class);
    private final ISessionService sessionService;
    private final HttpServletRequest request;

    @Autowired
    public ManageSessionAspect(ISessionService sessionService,
                               HttpServletRequest request) {
        this.sessionService = sessionService;
        this.request = request;
    }

    @Before("@annotation(validateSession)")
    public void checkSessionAndRole(ValidateSession validateSession) {
        String sessionToken = request.getHeader("Session-Token");

        if (sessionToken == null || sessionToken.isEmpty()) {
            logger.warn("Session token missing in request.");
            throw new SessionException("Session token is missing.");
        }

        boolean valid = sessionService.validateSessionToken(sessionToken, validateSession.role());

        if (!valid) {
            logger.warn("Invalid or unauthorized session token: {}", sessionToken);
            throw new SessionException("Invalid or unauthorized session token.");
        }

        logger.info("✅ Session validated for role: {}", validateSession.role());
    }


    @After("@annotation(InvalidateSession)")
    public void invalidateSession() {
        String sessionToken = request.getHeader("Session-Token");

        if (sessionToken != null && !sessionToken.isEmpty()) {
            if (sessionService.invalidateSession(sessionToken)) {
                logger.info("Session token invalidated: {}", sessionToken);
                return;
            }
            logger.warn("Attempted to invalidate a non-existent session token: {}", sessionToken);
        } else {
            logger.warn("No session token provided to invalidate.");
        }
    }
}
