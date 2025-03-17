package john.api1.application.components.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EndpointType {
    // admin
    UPLOAD_PET_PHOTO("/api/v1/admin/pets/register/{id}/upload-photo/{token}"),
    UPLOAD_REQUEST_PHOTO("/api/v1/admin/pets/request/{id}/upload-photo/{token}"),
    UPLOAD_REQUEST_VIDEO("/api/v1/admin/pets/request/{id}/upload-video/{token}");



    private final String endpoint;
}
