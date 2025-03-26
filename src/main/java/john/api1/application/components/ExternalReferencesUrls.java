package john.api1.application.components;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ExternalReferencesUrls {

    @Value("${app.frontend.url}")
    public String frontendDomainUrl;

    @Value("${app.springboot.url}")
    public String springbootApiUrl;
}
