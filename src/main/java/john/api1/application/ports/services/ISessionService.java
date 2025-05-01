package john.api1.application.ports.services;

import john.api1.common.session.SessionRole;

import java.util.Optional;

public interface ISessionService {
    String generateSessionToken();

    void saveSession(String token, SessionRole role);

    boolean validateSessionToken(String token, SessionRole role);

    Boolean invalidateSession(String token);

    Optional<SessionRole> getSessionRole(String token);
}

