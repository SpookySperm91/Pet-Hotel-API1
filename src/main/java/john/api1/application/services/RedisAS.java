package john.api1.application.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.VerificationGenerator;
import john.api1.application.components.enums.SessionRoleType;
import john.api1.application.services.response.SessionResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RedisAS {
    private final RedisTemplate<String, String> redisTemplate;
    private final String SESSION = "session:";
    private final String AUTHORIZED_ID = "authorizedId";
    private final String ROLE = "role";
    private final String USED = "used";

    public RedisAS(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Generate and save session token
    // Session format -> {token: "", authorizedId: "", role:""}
    // Session format one-time -> {token: "", authorizedId: "", role:"", used:""}
    public DomainResponse<SessionResponse> createRedisToken(String authorizedId, SessionRoleType session) {
        String token = VerificationGenerator.generateToken();
        String sessionKey = SESSION + token;  // Store session per token

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put(AUTHORIZED_ID, authorizedId);
        sessionData.put(ROLE, session.getSessionRole());

        if (session == SessionRoleType.ONE_TIME) {
            sessionData.put(USED, false);
        }

        redisTemplate.opsForHash().putAll(sessionKey, sessionData);
        Duration expiry = switch (session) {
            case ADMIN -> Duration.ofHours(4);
            case USER -> Duration.ofHours(2);
            case ONE_TIME -> Duration.ofMinutes(15);
        };

        redisTemplate.expire(sessionKey, expiry);
        return DomainResponse.success(new SessionResponse(authorizedId, token, session.getSessionRole()));
    }

    // Check session if valid
    public boolean isValidSession(String token, String authorizedId, SessionRoleType requiredRole) {
        String sessionKey = SESSION + token;

        Boolean exists = redisTemplate.hasKey(sessionKey);
        if (!exists) return false;

        String storedId = (String) redisTemplate.opsForHash().get(sessionKey, AUTHORIZED_ID);
        if (!authorizedId.equals(storedId)) return false;

        Object used = redisTemplate.opsForHash().get(sessionKey, USED);
        if ("true".equals(String.valueOf(used))) return false;

        String storedRole = (String) redisTemplate.opsForHash().get(sessionKey, ROLE);
        return hasRequiredRole(storedRole, requiredRole);
    }

    // Mark One-Time session as used to invalidate
    public void markSessionAsUsed(String token) {
        String sessionKey = SESSION + token;

        String role = (String) redisTemplate.opsForHash().get(sessionKey, ROLE);
        if (Objects.equals(role, SessionRoleType.ONE_TIME.getSessionRole())) {
            redisTemplate.opsForHash().put(sessionKey, USED, "true");
        }
    }

    private boolean hasRequiredRole(String storedRole, SessionRoleType requiredRole) {
        return switch (requiredRole) {
            case ADMIN -> Objects.equals(storedRole, SessionRoleType.ADMIN.getSessionRole());
            case USER -> Objects.equals(storedRole, SessionRoleType.USER.getSessionRole());
            case ONE_TIME -> Objects.equals(storedRole, SessionRoleType.ONE_TIME.getSessionRole());
        };
    }


}
