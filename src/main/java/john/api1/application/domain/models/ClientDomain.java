package john.api1.application.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Getter
public class ClientDomain {
    private final String id;
    private final String accountId;       // client's account
    private final List<String> petsId;    // client's pets

    // basics
    @Setter
    private String fullName;
    @Setter
    private String streetAddress;
    @Setter
    private String cityAddress;
    @Setter
    private String stateAddress;
    @Setter
    private String emergencyPhoneNumber;

    // time
    private final Instant createdAt;
    private Instant updatedAt;

    public ClientDomain(String id, String accountId, String fullName, List<String> petsId) {
        this.id = id;
        this.accountId = accountId;
        this.fullName = fullName;
        this.petsId = new ArrayList<>(Optional.ofNullable(petsId).orElse(Collections.emptyList()));
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public boolean addPet(String petId) {
        return Optional.ofNullable(petId)
                .filter(id -> !id.isBlank())
                .filter(id -> !petsId.contains(petId))
                .map(id -> {
                    this.petsId.add(petId);
                    updateTimestamp();
                    return true;
                })
                .orElse(false);
    }

    public boolean removePet(String petId) {
        return Optional.ofNullable(petId)
                .filter(id -> !id.isBlank())
                .filter(id -> petsId.remove(petId))
                .map(id -> {
                    updateTimestamp();
                    return true;
                })
                .orElse(false);
    }

    private void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
