package john.api1.application.domain.cores;

import john.api1.application.components.PasswordCreation;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.cores.custom_returns.CreateNewAccountResult;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientCreationDS {
    private final PasswordManagement passwordManagement;
    private final PasswordCreation passwordCreation;

    public ClientCreationDS(PasswordManagement passwordManagement, PasswordCreation passwordCreation) {
        this.passwordManagement = passwordManagement;
        this.passwordCreation = passwordCreation;
    }

    public CreateNewAccountResult createNewAccount(String email, String phoneNumber) {
        String rawPassword = passwordCreation.randomPassword();
        String hashedPassword = passwordManagement.hash(rawPassword);


        ClientAccountDomain account = new ClientAccountDomain(email, phoneNumber, hashedPassword);
        return new CreateNewAccountResult(rawPassword, account);
    }

    public ClientAccountDomain createNewAccount(String email, String phoneNumber, String password) {
        String hashedPassword = passwordManagement.hash(password);
        return new ClientAccountDomain(email, phoneNumber, hashedPassword, true);
    }

    public ClientDomain instantiateNewClient(
            String accountId,
            String fullName,
            String streetAddress,
            String cityAddress,
            String stateAddress,
            String emergencyPhoneNumber) {

        return new ClientDomain(
                accountId, fullName, null, streetAddress, cityAddress, stateAddress, emergencyPhoneNumber
        );
    }

    public static String mapAddress(PetOwnerCQRS owner) {
        return Stream.of(
                        owner.streetAddress(),
                        owner.cityAddress(),
                        owner.stateAddress()
                )
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(", "));
    }

}
