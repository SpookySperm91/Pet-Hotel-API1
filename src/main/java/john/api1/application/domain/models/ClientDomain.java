package john.api1.application.domain.models;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Getter
public class ClientDomain {
    private final String accountId;             // client's account
    private final List<String> petsId;          // client's pets

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

    public ClientDomain(String accountId, String fullName, List<String> petsId, String streetAddress, String cityAddress, String stateAddress, String emergencyPhoneNumber) {
        if (emergencyPhoneNumber != null && !isValidPhoneNumber(emergencyPhoneNumber))
            throw new IllegalArgumentException("Invalid emergency phone-number format or length");

        this.accountId = accountId;
        this.fullName = fullName;
        this.petsId = new ArrayList<>(Optional.ofNullable(petsId).orElse(Collections.emptyList()));
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.stateAddress = stateAddress;
        this.emergencyPhoneNumber = emergencyPhoneNumber;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public ClientDomain(String accountId, String fullName, List<String> petsId, String streetAddress, String cityAddress, String stateAddress, String emergencyPhoneNumber, Instant createdAt, Instant updatedAt) {
        this.accountId = accountId;
        this.fullName = fullName;
        this.petsId = new ArrayList<>(Optional.ofNullable(petsId).orElse(Collections.emptyList()));
        this.streetAddress = streetAddress;
        this.cityAddress = cityAddress;
        this.stateAddress = stateAddress;
        this.emergencyPhoneNumber = emergencyPhoneNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    // map string ids to objectId
    public List<ObjectId> getValidPetObjectIds() {
        return this.petsId != null
                ? this.petsId.stream()
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .toList()
                : Collections.emptyList();
    }

    private void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\+?\\d{10,}$");
    }


}
