package john.api1.application.adapters.services.email;

import john.api1.application.adapters.services.EmailBaseSend;
import john.api1.application.components.enums.EmailType;
import john.api1.application.domain.ports.repositories.ILoggingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class RegisteredEmail extends EmailBaseSend {
    private final TemplateEngine templateEngine;
    private final ILoggingRepository logging;

    @Autowired
    protected RegisteredEmail(JavaMailSender mailSender, TemplateEngine templateEngine, @Qualifier("MongoLoggingRepo") ILoggingRepository logging) {
        super(mailSender);
        this.templateEngine = templateEngine;
        this.logging = logging;
    }

    // Send Registered client's credentials to their email
    @Override
    protected String setEmailBody(String emailTemplate, String username, String body) {
        // Split once and validate body format
        String[] parts = parseBody(body);

        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("email", parts[0]);
        context.setVariable("phone", parts[1]);
        context.setVariable("password", parts[2]);

        return templateEngine.process(emailTemplate, context);
    }

    @Override
    protected String loadEmailTemplate() {
        return "email/registered";
    }


    @Override
    protected void saveErrorLog(String recipientEmail, String recipientUsername, String body, String errorMessage) {
        String emailType = EmailType.REGISTERED.getEmailType();
        logging.logFailedEmail(recipientEmail, recipientUsername, emailType, body, errorMessage);
    }


    private String[] parseBody(String body) {
        String[] parts = body.split("\\|");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid body format. Expected " + 3 + " parts.");
        }
        return parts;
    }
}


