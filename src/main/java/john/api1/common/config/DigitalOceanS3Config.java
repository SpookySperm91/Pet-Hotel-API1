package john.api1.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class DigitalOceanS3Config {
    @Value("${digital-ocean.region}")
    private String region;
    @Value("${digital-ocean.raw.url}")
    private String rawEndpoint;
    @Value("${digital-ocean.access.key}")
    private String accessKey;
    @Value("${digital-ocean.secret.key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(rawEndpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create(rawEndpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

}
