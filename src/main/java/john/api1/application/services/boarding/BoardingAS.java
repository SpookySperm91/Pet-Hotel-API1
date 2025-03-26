package john.api1.application.services.boarding;

import john.api1.application.ports.repositories.account.IAccountSearchRepository;
import john.api1.application.ports.repositories.pet.IPetsSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardingAS {
    private final IAccountSearchRepository accountSearch;
    private final IPetsSearchRepository petsSearch;

    @Autowired
    public BoardingAS(IAccountSearchRepository accountSearch, IPetsSearchRepository petsSearch) {
        this.accountSearch = accountSearch;
        this.petsSearch = petsSearch;
    }


}
