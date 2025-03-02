package john.api1.application.components;

import john.api1.application.components.enums.SmsType;
import john.api1.application.components.record.SmsRegisterContent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SmsTextContent {

    public static String registerMessage(SmsRegisterContent content) {
        return "Greetings from Big Paws Pet Hotel, Thank you " +content.username()+" for choosing our store. " +
                "Here is your login credentials, use either email or phone-number for user login. email: " + content.email()+ ", phone-number: " + content.phoneNumber() +
                ", password: " + content.password() + ". Visit our website 'bigpawspethotel.tech' or check our facebook to see the link" +
                ". Thank You. Note: Its recommend to change this password.";
    }
}
