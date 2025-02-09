package john.api1.common;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:application.properties")
public class MongoDBDocumentConfig {

    @Value("${mongodb.collection.member-accounts}")
    private String memberAccounts;

    @Value("${mongodb.collection.member-photos}")
    private String memberPhotos;

    @Value("${mongodb.collection.employee-accounts}")
    private String employeeAccounts;

    @Value("${mongodb.collection.employee-photos}")
    private String employeePhotos;
}
