package john.api1.application.domain.cores;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.PasswordCreation;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.cores.custom_returns.CreateNewAccountResult;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.ports.persistence.IAccountSearchRepository;

public class ClientCreationDS {
    private final IAccountSearchRepository accountSearch;
    private final PasswordManagement passwordManagement;
    private final PasswordCreation passwordCreation;

    public ClientCreationDS(IAccountSearchRepository accountSearch, PasswordManagement passwordManagement, PasswordCreation passwordCreation) {
        this.accountSearch = accountSearch;
        this.passwordManagement = passwordManagement;
        this.passwordCreation = passwordCreation;
    }

    public DomainResponse<CreateNewAccountResult> createNewAccount(String email, String phoneNumber) {
        // Validate inputs
        String error = validateAccountInputs(email, phoneNumber);
        if (error != null) return DomainResponse.error(error);

        // Check if either is already used
        if (accountSearch.getAccountByEmail(email).isPresent()) {
            return DomainResponse.error("ERROR: Email is already used");
        }
        if (accountSearch.getAccountByPhoneNumber(phoneNumber).isPresent()) {
            return DomainResponse.error("ERROR: Phone-number is already used");
        }

        String rawPassword = passwordCreation.randomPassword();
        String hashedPassword = passwordManagement.hash(rawPassword);
        ClientAccountDomain account = new ClientAccountDomain(email, phoneNumber, hashedPassword);

        return DomainResponse.success(new CreateNewAccountResult(rawPassword, account));
    }


    private String validateAccountInputs(String email, String phoneNumber) {
        if (email.isEmpty() || phoneNumber.isEmpty()) return "ERROR: Inputs are blank";
        if (!ClientAccountDomain.isValidEmail(email)) return "ERROR: Invalid email format";
        if (!ClientAccountDomain.isValidPhoneNumber(phoneNumber)) return "ERROR: Invalid phone-number format or length";
        return null; // No errors
    }


}
