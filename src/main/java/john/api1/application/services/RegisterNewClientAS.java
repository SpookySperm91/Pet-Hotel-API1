package john.api1.application.services;

import john.api1.application.async.AsyncEmailService;
import john.api1.application.components.DomainResponse;
import john.api1.application.adapters.services.email.body.EmailDetails;
import john.api1.application.adapters.services.email.body.RegistrationEmailData;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.exception.EmailSendingException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.ClientCreationDS;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.ports.repositories.IAccountSearchRepository;
import john.api1.application.ports.repositories.ICreateRepository;
import john.api1.application.dto.request.RegisterRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterNewClientAS {
    private final ClientCreationDS clientCreation;
    private final AsyncEmailService emailService;
    private final IAccountSearchRepository searchRepository;
    private final ICreateRepository createRepository;

    @Autowired
    public RegisterNewClientAS(ClientCreationDS clientCreation,
                               AsyncEmailService emailService,
                               @Qualifier("MongoSearchRepo") IAccountSearchRepository searchRepository,
                               @Qualifier("MongoCreateRepo") ICreateRepository createRepository) {
        this.clientCreation = clientCreation;
        this.emailService = emailService;
        this.searchRepository = searchRepository;
        this.createRepository = createRepository;
    }

    // Create account credential first
    // Instantiate user information then save to db
    // Return an ID of that newly registered client
    public DomainResponse<String> registerNewClient(RegisterRequestDTO request) {
        // Check if already exist
        Optional<String> validationError = validateRequest(request);
        if (validationError.isPresent()) {
            return DomainResponse.error(validationError.get());
        }

        try {
            var registerAccount = clientCreation.createNewAccount(request.getEmail(), request.getPhoneNumber());
            String rawPassword = registerAccount.rawPassword();
            ClientAccountDomain account = registerAccount.account();

            // send email async
            EmailDetails emailBody = new RegistrationEmailData(account.getEmail(), account.getPhoneNumber(), rawPassword);
            emailService.sendEmailAsync(EmailType.REGISTERED,
                    request.getFullName(), account.getEmail(), emailBody.format());

            ClientDomain information = clientCreation.instantiateNewClient(
                    account.getId(),
                    request.getFullName(),
                    request.getStreetAddress(),
                    request.getCityAddress(),
                    request.getStateAddress(),
                    request.getEmergencyPhoneNumber());

            return DomainResponse.success(createRepository.createNewClient(account, information));
        } catch (IllegalArgumentException | PersistenceException | EmailSendingException e) {
            return DomainResponse.error(e.getMessage());
        }
    }

    private Optional<String> validateRequest(RegisterRequestDTO request) {
        if (searchRepository.getAccountByEmail(request.getEmail()).isPresent()) {
            return Optional.of("ERROR: Email is already used");
        }
        if (searchRepository.getAccountByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            return Optional.of("ERROR: Phone-number is already used");
        }
        return Optional.empty();
    }

}
