package john.api1.application.services.user;

import john.api1.application.components.DomainResponse;
import john.api1.application.components.enums.SendStatus;
import john.api1.application.domain.cores.custom_returns.CheckLinkIfValidResult;
import john.api1.application.ports.repositories.IVerificationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordValidateAS {
    private final IVerificationRepository verification;

    @Autowired
    public ResetPasswordValidateAS(@Qualifier("MongoVerificationRepo") IVerificationRepository verification) {
        this.verification = verification;
    }

    public DomainResponse<CheckLinkIfValidResult> checkLinkIfValid(String id, String token) {
        if (!ObjectId.isValid(id)) {
            return DomainResponse.error(
                    new CheckLinkIfValidResult(SendStatus.INVALID, null),
                    "Id cannot be formatted");
        }

        // Check if valid or not
        var link = verification.getByIdAndValue(new ObjectId(id), token);

        if (link.isEmpty()) {
            return DomainResponse.error(
                    new CheckLinkIfValidResult(SendStatus.INVALID, null),
                    "Invalid verification link.");
        }

        if (!link.get().isValid()) {
            return DomainResponse.error(
                    new CheckLinkIfValidResult(SendStatus.EXPIRED, null),
                    "This verification link has expired.");
        }

        return DomainResponse.success(new CheckLinkIfValidResult(SendStatus.VALID, link.get().getAssociatedUsername()));
    }
}
