package john.api1.application.services;

import com.mongodb.MongoException;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.VerificationGenerator;
import john.api1.application.components.enums.EndpointType;
import john.api1.application.domain.models.TokenDomain;
import john.api1.application.ports.repositories.ITokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenAS {
    private final ITokenRepository tokenRepository;

    @Autowired
    public TokenAS(ITokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Generate token (token, authorized id, endpoint)
    // Save to db
    // Return domain object
    public DomainResponse<TokenDomain> createToken(String authorizedId, EndpointType endpoint) {
        try {
            String token = VerificationGenerator.generateToken();
            Instant now = Instant.now();
            Instant expiresAt = now.plusSeconds(600);

            TokenDomain newToken = new TokenDomain(
                    token, authorizedId, endpoint.getEndpoint(), now, expiresAt, false
            );

            TokenDomain savedToken = tokenRepository.save(newToken);
            return DomainResponse.success(savedToken, "Token authorized-id: " + authorizedId + " successfully created");
        } catch (DataAccessException | MongoException e) {
            return DomainResponse.error("Database error occurred. Please try again later.");
        }
    }

    // Check token if valid
    // Return response
    public DomainResponse<?> checkTokenValid(String token, String authorizedId, EndpointType endpoint) {
        try {
            TokenDomain tokenDomain = tokenRepository
                    .findValidToken(token, authorizedId, endpoint.getEndpoint())
                    .orElseThrow(() -> new IllegalArgumentException("Error: Token not valid or expired"));

            if (tokenDomain.isUsed()) {
                return DomainResponse.error("Error: Token has already been used.");
            }

            if (tokenDomain.getExpiredAt().isBefore(Instant.now())) {
                return DomainResponse.error("Error: Token has expired.");
            }

            return DomainResponse.success("Token is valid.");
        } catch (IllegalArgumentException e) {
            return DomainResponse.error(e.getMessage());
        } catch (DataAccessException | MongoException e) {
            return DomainResponse.error("Database error occurred. Please try again later.");
        }
    }


    public DomainResponse<?> invalidateToken(String token, String authorizedId, EndpointType endpoint) {
        try {
            if (!tokenRepository.markAsUsed(token, authorizedId, endpoint.getEndpoint())) {
                return DomainResponse.error("Error: Token not found or already invalidated.");
            }

            return DomainResponse.success("Token '" + token + "' successfully invalidated");
        } catch (RuntimeException e) {
            return DomainResponse.error("Database error occurred. Please try again later.");
        }
    }
}
