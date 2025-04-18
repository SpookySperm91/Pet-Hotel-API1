package john.api1.application.services.admin;

import john.api1.application.adapters.services.email.body.EmailDetails;
import john.api1.application.adapters.services.email.body.RegistrationEmailData;
import john.api1.application.async.AsyncEmailService;
import john.api1.application.components.DomainResponse;
import john.api1.application.components.SmsTextContent;
import john.api1.application.components.enums.EmailType;
import john.api1.application.components.enums.SmsType;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.EmailSendingException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.components.record.SmsRegisterContent;
import john.api1.application.domain.cores.ClientCreationDS;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.domain.models.ClientDomain;
import john.api1.application.domain.models.EmailLogsDomain;
import john.api1.application.domain.models.SmsLogDomain;
import john.api1.application.dto.request.RegisterRDTO;
import john.api1.application.ports.repositories.ILogEmailRepository;
import john.api1.application.ports.repositories.ILogSmsRepository;
import john.api1.application.ports.repositories.owner.IAccountCreateRepository;
import john.api1.application.ports.repositories.owner.IAccountSearchRepository;
import john.api1.application.ports.services.IRegisterNewClient;
import john.api1.application.ports.services.history.IHistoryLogCreate;
import john.api1.application.services.response.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class RegisterNewClientAS implements IRegisterNewClient {
    private static final Logger logger = LoggerFactory.getLogger(RegisterNewClientAS.class);

    private final ClientCreationDS clientCreation;
    private final AsyncEmailService emailService;
    private final IAccountSearchRepository searchRepository;
    private final IAccountCreateRepository createRepository;
    private final ILogEmailRepository logEmailRepository;
    private final ILogSmsRepository logSmsRepository;
    private final IHistoryLogCreate historyLog;

    @Autowired
    public RegisterNewClientAS(ClientCreationDS clientCreation,
                               AsyncEmailService emailService,
                               @Qualifier("MongoAccountSearchRepo") IAccountSearchRepository searchRepository,
                               @Qualifier("MongoCreateRepo") IAccountCreateRepository createRepository,
                               @Qualifier("MongoEmailLogRepo") ILogEmailRepository logEmailRepository,
                               @Qualifier("MongoSmsLogRepo") ILogSmsRepository logSmsRepository,
                               IHistoryLogCreate historyLog) {
        this.clientCreation = clientCreation;
        this.emailService = emailService;
        this.searchRepository = searchRepository;
        this.createRepository = createRepository;
        this.logEmailRepository = logEmailRepository;
        this.logSmsRepository = logSmsRepository;
        this.historyLog = historyLog;
    }

    // Create account credential first
    // Instantiate user information then save to db
    // Log and send email to recipient
    // Return custom response(id, fileName, sms number and text, success)
    public DomainResponse<RegisterResponse> registerNewClient(RegisterRDTO request) {
        // Check if already exist
        Optional<String> validationError = validateRequest(request);
        if (validationError.isPresent()) {
            return DomainResponse.error(validationError.get());
        }

        try {
            var registerAccount = clientCreation.createNewAccount(request.getEmail(), request.getPhoneNumber());
            String rawPassword = registerAccount.rawPassword();
            ClientAccountDomain account = registerAccount.account();

            // Instantiate and save new registered to db
            ClientDomain information = clientCreation.instantiateNewClient(
                    account.getId(),
                    request.getFullName(),
                    request.getStreetAddress(),
                    request.getCityAddress(),
                    request.getStateAddress(),
                    request.getEmergencyPhoneNumber());
            String registeredId = createRepository.createNewClient(account, information);

            // Email mechanism
            EmailDetails emailBody = new RegistrationEmailData(account.getEmail(), account.getPhoneNumber(), rawPassword);
            logAndSendEmail(registeredId, account.getEmail(), request.getFullName(), emailBody.format());

            // SMS Message
            String smsBody = SmsTextContent.registerMessage(new SmsRegisterContent(request.getFullName(), account.getEmail(), account.getPhoneNumber(), rawPassword));
            logSms(registeredId, account.getPhoneNumber(), request.getFullName(), smsBody);

            String message = String.format("New pet owner '%s' has been successfully registered.", request.getFullName());

            return DomainResponse.success(
                    new RegisterResponse(
                            registeredId,
                            request.getFullName(),
                            request.getEmail(),
                            request.getPhoneNumber(),
                            smsBody), message);


        } catch (DomainArgumentException | PersistenceException | EmailSendingException e) {
            logger.error("Error registering client: {}", e.getMessage(), e);
            return DomainResponse.error(exceptionMessage(e));
        }
    }

    // Log email first
    // Send email to the recipient
    private void logAndSendEmail(String ownerId, String email, String fullName, String body) {
        EmailLogsDomain emailLog = EmailLogsDomain.createNewLog(ownerId, email, fullName, EmailType.REGISTERED, body);

        CompletableFuture.runAsync(() -> logEmailRepository.logEmail(emailLog));
        emailService.sendEmailAsync(EmailType.REGISTERED, fullName, email, body)
                .doOnError(error ->
                        logger.error("Error sending email to {}: {}", email, error.getMessage()))
                .subscribe();
    }

    // Log SMS
    private void logSms(String ownerId, String phoneNumber, String fullName, String textBody) {
        SmsLogDomain smsLog = SmsLogDomain.createNewLog(ownerId, phoneNumber, fullName, SmsType.REGISTER, textBody);
        logSmsRepository.logSmsText(smsLog);
    }


    private Optional<String> validateRequest(RegisterRDTO request) {
        boolean emailExists = searchRepository.getAccountByEmail(request.getEmail()).isPresent();
        boolean phoneExists = searchRepository.getAccountByPhoneNumber(request.getPhoneNumber()).isPresent();

        return emailExists ? Optional.of("Email is already used") :
                phoneExists ? Optional.of("Phone-number is already used") :
                        Optional.empty();
    }

    private String exceptionMessage(RuntimeException e) {
        return (e instanceof DomainArgumentException) ? e.getMessage()
                : (e instanceof PersistenceException) ? "A database error occurred. Please try again."
                : (e instanceof EmailSendingException) ? "Failed to send registration email. Please check the email service."
                : "An unexpected error occurred. Please contact support.";
    }
}
