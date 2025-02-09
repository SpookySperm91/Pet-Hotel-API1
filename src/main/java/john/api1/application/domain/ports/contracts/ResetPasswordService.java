package john.api1.application.domain.ports.contracts;

public interface ResetPasswordService {
    boolean checkVerificationCode(Object account, int verificationCode);        // phone-base code
    boolean sendEmailResetPasswordLink(String email);                           // email-base link
    void resetPassword(Object account, String newPassword);
}
