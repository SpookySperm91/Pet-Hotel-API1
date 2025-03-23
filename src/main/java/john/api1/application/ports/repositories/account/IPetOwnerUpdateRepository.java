package john.api1.application.ports.repositories.account;

public interface IPetOwnerUpdateRepository {
    // pet
    boolean addNewPet(String petOwnerId, String petId);
    boolean removePet(String petOwnerId, String petId);

    // not so important
    boolean updateFullName(String petOwnerId, String fullName);
    boolean updateStreetAddress(String petOwnerId, String streetAddress);
    boolean updateCityAddress(String petOwnerId, String cityAddress);
    boolean updateStateAddress(String petOwnerId, String stateAddress);
    boolean updateEmergencyPhoneNumber(String petOwnerId, String phoneNumber);
}
