package john.api1.application.ports.repositories.wrapper;

import java.time.Instant;

public record PreSignedUrlResponse(String preSignedUrl, Instant expiresAt) {}
