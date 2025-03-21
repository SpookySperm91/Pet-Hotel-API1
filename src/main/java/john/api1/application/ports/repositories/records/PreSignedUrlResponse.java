package john.api1.application.ports.repositories.records;

import java.time.Instant;

public record PreSignedUrlResponse(String preSignedUrl, Instant expiresAt) {}
