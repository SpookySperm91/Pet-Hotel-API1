package john.api1.application.domain.ports.contracts;

public interface AuthenticationService {
    boolean checkCredentials(String email, String password);
}
