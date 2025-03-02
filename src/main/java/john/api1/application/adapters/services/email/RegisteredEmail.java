package john.api1.application.adapters.services.email;

import john.api1.application.adapters.services.EmailBaseSend;
import john.api1.application.components.enums.EmailType;
import john.api1.application.ports.repositories.ILoggingRepository;
import john.api1.common.config.MailGunConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@Qualifier("RegisteredEmail")
public class RegisteredEmail extends EmailBaseSend {
    private final TemplateEngine templateEngine;

    @Autowired
    protected RegisteredEmail(MailGunConfig mail, @Qualifier("mailgunWebClient") WebClient web, TemplateEngine templateEngine) {
        super(mail, web);
        this.templateEngine = templateEngine;
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



    private String[] parseBody(String body) {
        String[] parts = body.split("\\|");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid body format. Expected " + 3 + " parts.");
        }
        return parts;
    }
}


