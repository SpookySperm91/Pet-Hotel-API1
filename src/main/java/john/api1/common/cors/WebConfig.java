package john.api1.common.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${domain.endpoint.pet-owner}")
    private String petOwnerEndpoint;

    @Value("${domain.endpoint.admin}")
    private String adminEndpoint;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow pet owner frontend to access pet owner backend routes
        registry.addMapping("/api/v1/pet-owner/**")
                .allowedOrigins(petOwnerEndpoint)
                .allowedOrigins("http://localhost:5000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");

        registry.addMapping("/api/v1/pet-owner/**")
                .allowedOrigins("http://localhost:5000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");


        // Allow admin frontend to access admin backend routes
        registry.addMapping("/api/v1/admin/**")
                .allowedOrigins(adminEndpoint)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}






