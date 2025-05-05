package john.api1.application.services.admin;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.ports.repositories.owner.IAccountSearchRepository;
import john.api1.application.ports.repositories.owner.IAccountUpdateRepository;
import john.api1.application.ports.services.IRegisterClientApprove;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterClientApproveAS implements IRegisterClientApprove {
    private final IAccountSearchRepository ownerSearch;
    private final IAccountUpdateRepository ownerUpdate;

    @Autowired
    public RegisterClientApproveAS(IAccountSearchRepository ownerSearch,
                                   IAccountUpdateRepository ownerUpdate) {
        this.ownerSearch = ownerSearch;
        this.ownerUpdate = ownerUpdate;
    }

    @Override
    public DomainResponse<Void> approvePendingPetOwnerAccount(String id) {
        try {
            if (!ObjectId.isValid(id))
                throw new PersistenceException("Invalid account id cannot be converted to ObjectId");

            var search = ownerSearch.getAccountById(id);
            if (search.isEmpty()) throw new PersistenceException("Pet owner account cannot be found");

            ClientAccountDomain account = search.get();
            account = account.approveAccount();
            ownerUpdate.updateAccount(account);
            return DomainResponse.success("Successfully approve pending pet owner account!");
        } catch (DomainArgumentException | PersistenceException e) {
            return DomainResponse.error(e.getMessage());
        } catch (Exception f) {
            return DomainResponse.error("Something wrong with the system. Try again later");
        }
    }
}
