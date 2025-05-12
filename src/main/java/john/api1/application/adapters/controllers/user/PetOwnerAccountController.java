package john.api1.application.adapters.controllers.user;

import john.api1.application.domain.models.ClientAccountDomain;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.request.ChangeCreditRDTO;
import john.api1.application.ports.repositories.owner.IAccountSearchRepository;
import john.api1.application.ports.repositories.owner.IAccountUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pet-owner/account")
public class PetOwnerAccountController {
    private final IAccountSearchRepository accountSearch;
    private final IAccountUpdateRepository accountUpdate;

    @Autowired
    public PetOwnerAccountController(IAccountSearchRepository accountSearch,
                                     IAccountUpdateRepository accountUpdate) {
        this.accountSearch = accountSearch;
        this.accountUpdate = accountUpdate;
    }

    @PutMapping("/change-details")
    public ResponseEntity<DTOResponse<String>> changeCredentials(
            @Valid @RequestBody ChangeCreditRDTO request,
            BindingResult result) {
        try {
            if (request.getId().trim().isEmpty()) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid pet owner id");
            }

            var search = accountSearch.getAccountById(request.getId());
            if (search.isEmpty()) return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid account");

            ClientAccountDomain account = search.get();

            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                account = account.changeEmail(request.getEmail());
                System.out.println(account.getEmail());
            }
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
                account = account.changePhoneNumber(request.getPhoneNumber());
                System.out.println(account.getPhoneNumber());
            }

            accountUpdate.updateAccount(account);

            return ResponseEntity.status(HttpStatus.OK).body(
                    DTOResponse.of(HttpStatus.OK.value(), account.getId(), "Account successfully updated")
            );
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurs in the system. Try again");
        }
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }
}
