package john.api1.application.ports.repositories;

import john.api1.application.domain.models.VerificationDomain;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface IVerificationRepository {
    String save(VerificationDomain verification);

    Optional<VerificationDomain> getById(String id);

    Optional<VerificationDomain> getByValue(String value);

    Optional<VerificationDomain> getByIdAndValue(ObjectId id, String value);

    Optional<VerificationDomain> updateStatusToUsed(String id);

    boolean updateStatusToUsed(String associatedId, String value);

    Optional<Boolean> deleteById(String id);

    int deleteAllUsed();
}
