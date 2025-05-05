package john.api1.application.adapters.services.email;

import john.api1.common.config.MailGunConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@Qualifier("AdminPasswordResetLink")
public class AdminPasswordLink extends EmailBaseSend {
    private final TemplateEngine templateEngine;

    public AdminPasswordLink(MailGunConfig mail, @Qualifier("mailgunWebClient") WebClient webClient, TemplateEngine templateEngine) {
        super(mail, webClient);
        this.templateEngine = templateEngine;
    }

    @Override
    protected String setEmailBody(String emailTemplate, String username, String body) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("link", body);

        return templateEngine.process(emailTemplate, context);
    }

    @Override
    protected String loadEmailTemplate() {
        return "email/admin-password-link";
    }
}

