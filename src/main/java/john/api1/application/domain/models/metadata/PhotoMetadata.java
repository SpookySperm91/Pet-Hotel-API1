package john.api1.application.domain.models.metadata;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class PhotoMetadata {
    private String photoUrl;
    private String fileType;
    private long photoSize;
    private String photoDimensions;
    private Instant createAt;
    private Instant updateAt;
}
