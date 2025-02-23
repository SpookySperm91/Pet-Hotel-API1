package john.api1.application.adapters.services;

import jakarta.mail.internet.MimeMessage;
import john.api1.application.components.exception.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public abstract class EmailBaseSend {
    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailBaseSend.class);

    protected EmailBaseSend(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // abstracts
    protected abstract String loadEmailTemplate();

    protected abstract String setEmailBody(String emailTemplate, String username, String body);

    protected abstract void saveErrorLog(String recipientEmail, String recipientUsername, String body, String errorMessage);

    public void sendEmail(String username, String email, String body) {
        try {
            String emailTemplate = loadEmailTemplate();
            String emailBody = setEmailBody(emailTemplate, username, body);  // Fix: Using 'body' here

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Email Notification");
            helper.setText(emailBody, true); // true = send as HTML email

            mailSender.send(message);
            logger.info("✅ Email sent successfully to: {}", email);
        } catch (Exception e) {
            // log error to db
            String errorMessage = "❌ Error sending email to " + email + ": " + e.getMessage();
            saveErrorLog(email, username, body, errorMessage);

            // system log
            logger.error("❌ Error sending email to {}: {}", email, e.getMessage());
            throw new EmailSendingException("Failed to send email to " + email, e);
        }
    }
}
