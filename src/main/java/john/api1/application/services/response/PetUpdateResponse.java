package john.api1.application.services.response;

import jakarta.annotation.Nullable;

public record PetUpdateResponse(String petId,
                                @Nullable String newPetName,
                                @Nullable String type,
                                @Nullable String breed,
                                @Nullable String profilePicUrl) {
}
