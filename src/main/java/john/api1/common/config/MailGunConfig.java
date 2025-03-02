package john.api1.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:application.properties")
public class MailGunConfig {

    @Value("${mail.gun.api.key}")
    private String apiKey;
}
