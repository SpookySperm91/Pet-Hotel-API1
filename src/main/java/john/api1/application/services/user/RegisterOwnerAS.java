package john.api1.application.services.user;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.exception.PersistenceHistoryException;
import john.api1.application.domain.cores.ClientCreationDS;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.dto.request.RegisterOwnerRDTO;
import john.api1.application.dto.request.RegisterRDTO;
import john.api1.application.ports.repositories.owner.IAccountCreateRepository;
import john.api1.application.ports.repositories.owner.IAccountSearchRepository;
import john.api1.application.ports.services.IRegisterNewClient;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import john.api1.application.services.admin.RegisterNewClientAS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Qualifier("RegisterOwnerAS")
public class RegisterOwnerAS implements IRegisterNewClient<RegisterOwnerRDTO, String> {
    private static final Logger log = LoggerFactory.getLogger(RegisterNewClientAS.class);
    private final ClientCreationDS clientCreation;
    private final IAccountSearchRepository searchRepository;
    private final IAccountCreateRepository createRepository;
    private final IHistoryLogCreate historyLog;

    @Autowired
    public RegisterOwnerAS(ClientCreationDS clientCreation,
                           @Qualifier("MongoAccountSearchRepo") IAccountSearchRepository searchRepository,
                           @Qualifier("MongoCreateRepo") IAccountCreateRepository createRepository,
                           IHistoryLogCreate historyLog) {
        this.clientCreation = clientCreation;
        this.searchRepository = searchRepository;
        this.createRepository = createRepository;
        this.historyLog = historyLog;
    }


    @Override
    public DomainResponse<String> registerNewClient(RegisterOwnerRDTO request) {
        Optional<String> validationError = validateRequest(request);
        if (validationError.isPresent()) return DomainResponse.error(validationError.get());

        try {
            var registerAccount = clientCreation.createNewAccount(request.getEmail(), request.getPhoneNumber(), request.getPassword());
            ClientDomain information = clientCreation.instantiateNewClient(
                    null,
                    request.getFullName(),
                    request.getStreetAddress(),
                    request.getCityAddress(),
                    request.getStateAddress(),
                    request.getEmergencyPhoneNumber());
            String registeredId = createRepository.createNewClient(registerAccount, information);
            log.info("Successfully create new account. Currently pending to be approved:");

            try {
                historyLog.createActivityLogOwnerRegisterByOwner(registeredId, information.getFullName());
                log.info("Activity log created for pending registered owner '{}'", information.getFullName());
            } catch (PersistenceHistoryException e) {
                log.warn("Activity log for new owner registration failed to save in class 'RegisterOwnerAS'");
            }

            return DomainResponse.success(registeredId, "Successfully create new account, currently locked and pending await to approved");
        } catch (DomainArgumentException | PersistenceException e) {
            log.error("Error registering client: {}", e.getMessage(), e);
            return DomainResponse.error(e.getMessage());
        }
    }


    private Optional<String> validateRequest(RegisterRDTO request) {
        boolean emailExists = searchRepository.getAccountByEmail(request.getEmail()).isPresent();
        boolean phoneExists = searchRepository.getAccountByPhoneNumber(request.getPhoneNumber()).isPresent();

        return emailExists ? Optional.of("Email is already used") :
                phoneExists ? Optional.of("Phone-number is already used") :
                        Optional.empty();
    }

}
