package john.api1.application.adapters.repositories.verification;

import john.api1.application.adapters.repositories.TokenEntity;
import john.api1.application.domain.models.TokenDomain;
import john.api1.application.ports.repositories.ITokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class TokenRepositoryMongo implements ITokenRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TokenRepositoryMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public TokenDomain save(TokenDomain token) {
        TokenEntity tokenEntity = new TokenEntity(
                null,
                token.getToken(),
                token.getAuthorizedId(),
                token.getEndpoint(),
                token.getCreatedAt(),
                token.getExpiredAt(),
                token.isUsed()
        );

        mongoTemplate.save(tokenEntity);
        return TokenDomain.copyOf(token);
    }

    @Override
    public Optional<TokenDomain> findByToken(String token) {
        Query query = new Query(Criteria.where("token").is(token));
        TokenEntity entity = mongoTemplate.findOne(query, TokenEntity.class);
        return Optional.ofNullable(entity).map(this::entityToDomain);
    }

    @Override
    public Optional<TokenDomain> findValidToken(String token, String authorizedId, String endpoint) {
        Query query = new Query(
                Criteria.where("token").is(token)
                        .and("authorizedId").is(authorizedId)
                        .and("endpoint").is(endpoint)
                        .and("used").is(false)
                        .and("expiredAt").gt(Instant.now())
        );
        TokenEntity entity = mongoTemplate.findOne(query, TokenEntity.class);
        return Optional.ofNullable(entity).map(this::entityToDomain);
    }

    @Override
    public void markAsUsed(String token) {
        Query query = new Query(Criteria.where("token").is(token));
        Update update = new Update().set("used", true);
        mongoTemplate.updateFirst(query, update, TokenEntity.class);
    }

    @Override
    public void deleteByExpiredAtBefore(Instant now) {
        Query query = new Query(Criteria.where("expiredAt").lt(now));
        mongoTemplate.remove(query, TokenEntity.class);
    }

    private TokenDomain entityToDomain(TokenEntity entity) {
        return new TokenDomain(
                entity.getToken(),
                entity.getAuthorizedId(),
                entity.getEndpoint(),
                entity.getCreatedAt(),
                entity.getExpiredAt(),
                entity.isUsed()
        );
    }
}
