package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EndpointType {
    // admin
    UPLOAD_PET_PHOTO("/api/v1/admin/pets/register/{id}/{petName}/upload-photo/{token}"),
    UPLOAD_REQUEST_PHOTO("/api/v1/admin/pets/request/{id}/upload-photo/{token}"),
    UPLOAD_REQUEST_VIDEO("/api/v1/admin/pets/request/{id}/upload-video/{token}");

    private final String endpoint;

    public String getFormattedEndpoint(String id, String petName, String token) {
        if (!endpoint.contains("{petName}")) {
            throw new IllegalArgumentException("This endpoint does not require petName!");
        }
        return endpoint
                .replace("{id}", id)
                .replace("{petName}", petName)
                .replace("{token}", token);
    }


    public String getFormattedEndpoint(String id, String token) {
        return endpoint
                .replace("{id}", id)
                .replace("{token}", token);
    }
}
