package john.api1.application.domain.cores.config;

import john.api1.application.components.PasswordCreation;
import john.api1.application.components.PasswordManagement;
import john.api1.application.domain.cores.ClientCreationDS;
import john.api1.application.domain.cores.ClientLoginDS;
import john.api1.application.ports.repositories.IAccountSearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public ClientCreationDS clientCreationDS(PasswordManagement passwordManagement, PasswordCreation passwordCreation) {
        return new ClientCreationDS(passwordManagement, passwordCreation);
    }

    @Bean
    public ClientLoginDS clientLoginDS(PasswordManagement passwordManagement) {
        return new ClientLoginDS(passwordManagement);
    }
}
