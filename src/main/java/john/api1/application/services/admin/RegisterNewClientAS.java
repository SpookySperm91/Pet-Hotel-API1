package john.api1.application.services.admin;

import john.api1.application.adapters.services.email.body.EmailDetails;
import john.api1.application.adapters.services.email.body.RegistrationEmailData;
import john.api1.application.async.AsyncEmailService;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.SmsTextContent;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.exception.EmailSendingException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.record.SmsRegisterContent;
import john.api1.application.domain.cores.ClientCreationDS;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.domain.models.EmailLogsDomain;
import john.api1.application.dto.request.RegisterRequestDTO;
import john.api1.application.ports.repositories.IAccountSearchRepository;
import john.api1.application.ports.repositories.ICreateRepository;
import john.api1.application.ports.repositories.ILogEmailRepository;
import john.api1.application.services.response.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterNewClientAS {
    private static final Logger logger = LoggerFactory.getLogger(RegisterNewClientAS.class);

    private final ClientCreationDS clientCreation;
    private final AsyncEmailService emailService;
    private final IAccountSearchRepository searchRepository;
    private final ICreateRepository createRepository;
    private final ILogEmailRepository logEmailRepository;

    @Autowired
    public RegisterNewClientAS(ClientCreationDS clientCreation,
                               AsyncEmailService emailService,
                               @Qualifier("MongoAccountSearchRepo") IAccountSearchRepository searchRepository,
                               @Qualifier("MongoCreateRepo") ICreateRepository createRepository,
                               @Qualifier("MongoEmailLogRepo") ILogEmailRepository logEmailRepository) {
        this.clientCreation = clientCreation;
        this.emailService = emailService;
        this.searchRepository = searchRepository;
        this.createRepository = createRepository;
        this.logEmailRepository = logEmailRepository;
    }

    // Create account credential first
    // Instantiate user information then save to db
    // Log and send email to recipient
    // Return custom response(id, name, sms number and text, success)
    public DomainResponse<RegisterResponse> registerNewClient(RegisterRequestDTO request) {
        // Check if already exist
        Optional<String> validationError = validateRequest(request);
        if (validationError.isPresent()) {
            return DomainResponse.error(validationError.get());
        }

        try {
            var registerAccount = clientCreation.createNewAccount(request.getEmail(), request.getPhoneNumber());
            String rawPassword = registerAccount.rawPassword();
            ClientAccountDomain account = registerAccount.account();

            // EMAIL mechanism
            EmailDetails emailBody = new RegistrationEmailData(account.getEmail(), account.getPhoneNumber(), rawPassword);
            logAndSendEmail(account.getEmail(), request.getFullName(), emailBody.format());

            // SMS Message
            String smsBody = SmsTextContent.registerMessage(
                    new SmsRegisterContent(
                            request.getFullName(),
                            account.getEmail(),
                            account.getPhoneNumber(),
                            rawPassword));

            ClientDomain information = clientCreation.instantiateNewClient(
                    account.getId(),
                    request.getFullName(),
                    request.getStreetAddress(),
                    request.getCityAddress(),
                    request.getStateAddress(),
                    request.getEmergencyPhoneNumber());

            String registeredId = createRepository.createNewClient(account, information);
            return DomainResponse.success(
                    new RegisterResponse(
                            registeredId,
                            request.getFullName(),
                            request.getEmail(),
                            request.getPhoneNumber(),
                            smsBody));

        } catch (IllegalArgumentException | PersistenceException | EmailSendingException e) {
            logger.error("Error registering client: {}", e.getMessage(), e);
            return DomainResponse.error(exceptionMessage(e));
        }
    }

    // Log email first
    // Send email to the recipient
    private void logAndSendEmail(String email, String fullName, String body) {
        EmailLogsDomain emailLog = EmailLogsDomain.createNewLog(email, fullName, EmailType.REGISTERED, body);
        logEmailRepository.logEmail(emailLog);

        emailService.sendEmailAsync(EmailType.REGISTERED, fullName, email, body)
                .doOnError(error ->
                        logger.error("Error sending email to {}: {}", email, error.getMessage()))
                .subscribe();
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

    private String exceptionMessage(RuntimeException e) {
        return (e instanceof IllegalArgumentException) ? "Invalid input provided."
                : (e instanceof PersistenceException) ? "A database error occurred. Please try again."
                : (e instanceof EmailSendingException) ? "Failed to send registration email. Please check the email service."
                : "An unexpected error occurred. Please contact support.";
    }


}
