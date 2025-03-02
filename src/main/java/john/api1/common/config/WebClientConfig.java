package john.api1.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private static final String MAILGUN_BASE_URL = "https://api.mailgun.net/v3";

    @Bean
    public WebClient mailgunWebClient(WebClient.Builder webClientBuilder){

        return webClientBuilder
                .baseUrl(MAILGUN_BASE_URL)
                .build();
    }
}
