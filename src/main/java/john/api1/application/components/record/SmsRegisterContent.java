package john.api1.application.components.record;

import org.springframework.stereotype.Component;


public record SmsRegisterContent(String username, String email, String phoneNumber, String password) {
}
