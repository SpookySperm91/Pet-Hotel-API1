package john.api1.application.ports.repositories;

import john.api1.application.adapters.repositories.TokenEntity;
import john.api1.application.domain.models.TokenDomain;

import java.time.Instant;
import java.util.Optional;

public interface ITokenRepository {
    TokenDomain save(TokenDomain token);
    Optional<TokenDomain> findByToken(String token);
    Optional<TokenDomain> findValidToken(String token, String authorizedId, String endpoint);
    void markAsUsed(String token);
    void deleteByExpiredAtBefore(Instant now);
}

