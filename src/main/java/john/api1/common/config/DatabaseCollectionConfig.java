package john.api1.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DatabaseCollectionConfig {

    @Value("${db.collection.admin}")
    private String admin;

    @Value("${db.collection.pet.owner}")
    private String petOwner;

    @Value("${db.collection.notification}")
    private String notification;

    @Value("${db.collection.pet}")
    private String pet;

    @Value("${db.collection.boarding}")
    private String boarding;

    @Value("${db.collection.price.breakdown}")
    private String priceBreakdown;

    @Value("${db.collection.request}")
    private String request;

    @Value("${db.collection.request.photo}")
    private String requestPhoto;

    @Value("${db.collection.request.video}")
    private String requestVideo;

    @Value("${db.collection.request.extension}")
    private String requestExtension;

    @Value("${db.collection.request.grooming}")
    private String requestGrooming;

    @Value("${db.collection.history}")
    private String history;

    @Value("${db.collection.storage}")
    private String storage;

    @Value("${db.collection.verification}")
    private String verification;

    @Value("${db.collection.sms}")
    private String sms;

    @Value("${db.collection.token}")
    private String token;

    @Value("${db.collection.failed.email}")
    private String failedEmail;

    @Value("${db.collection.email.log}")
    private String emailLog;
}
