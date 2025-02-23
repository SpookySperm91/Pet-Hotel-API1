package john.api1.application.ports.contracts;

public interface AuthenticationService {
    boolean checkCredentials(String email, String password);
}
