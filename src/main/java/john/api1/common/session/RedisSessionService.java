package john.api1.common.session;

import john.api1.application.ports.services.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisSessionService implements ISessionService {
    private final Logger logger = LoggerFactory.getLogger(RedisSessionService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SESSION_PREFIX = "session:";
    private static final String ROLE_KEY = "role";
    private static final int TIMEOUT = 45;

    @Autowired
    public RedisSessionService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String generateSessionToken() {
        return UUID.randomUUID().toString(); // Generate token
    }

    @Override
    public void saveSession(String token, SessionRole role) {
        String redisKey = SESSION_PREFIX + token;
        redisTemplate.opsForHash().put(redisKey, ROLE_KEY, role.getSessionRole());
        redisTemplate.expire(redisKey, TIMEOUT, TimeUnit.MINUTES);
    }

    @Override
    public boolean validateSessionToken(String token, SessionRole role) {
        String redisKey = SESSION_PREFIX + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            String storedRole = (String) redisTemplate.opsForHash().get(redisKey, ROLE_KEY);
            return role.getSessionRole().equals(storedRole);
        }
        return false;
    }

    @Override
    public Boolean invalidateSession(String token) {
       return redisTemplate.delete(SESSION_PREFIX + token);
    }

    @Override
    public Optional<SessionRole> getSessionRole(String token) {
        try {
            String redisKey = SESSION_PREFIX + token;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                String stored = (String) redisTemplate.opsForHash().get(redisKey, ROLE_KEY);
                return Optional.of(SessionRole.fromValue(stored)); // assuming fromValue exists
            }
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid session type hash-key", e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error while fetching session role for token: " + token, e);
            return Optional.empty();
        }
    }
}
