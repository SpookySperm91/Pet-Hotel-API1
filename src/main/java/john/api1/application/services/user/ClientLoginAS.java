package john.api1.application.services.user;

import john.api1.application.domain.cores.ClientLoginDS;
import john.api1.application.ports.repositories.IAccountSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ClientLoginAS {
    private final IAccountSearchRepository searchRepository;
    private final ClientLoginDS clientLogin;

    @Autowired
    public ClientLoginAS(@Qualifier("MongoSearchRepo") IAccountSearchRepository searchRepository, ClientLoginDS clientLogin) {
        this.searchRepository = searchRepository;
        this.clientLogin = clientLogin;
    }

    public void checkEmail(String email) {
        try {
            var userEmail = searchRepository.getAccountByEmail(email);

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void checkPhoneNumber(String phoneNumber) {
        try {
            var userEmail = searchRepository.getAccountByPhoneNumber(phoneNumber);

        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
