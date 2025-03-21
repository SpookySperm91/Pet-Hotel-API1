package john.api1.application.adapters.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetAdapter {
    private static String domainUrl;
    private static final String tempUrl = "http://localhost:5000";

    @Value("${app.frontend.url}")
    public void setDomainUrl(String url) {
        domainUrl = url;
    }

    public static String generateResetLink(String userId, String resetToken) {
        return tempUrl + "/reset-password.html?id=" + userId + "&token=" + resetToken;
    }
}
