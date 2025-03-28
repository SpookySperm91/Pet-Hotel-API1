package john.api1.application.adapters.repositories.boarding;

import com.google.common.base.Optional;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.ports.repositories.boarding.IBoardingCreateRepository;
import john.api1.application.ports.repositories.boarding.IBoardingManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BoardingManageRepository implements IBoardingCreateRepository, IBoardingManagementRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public BoardingManageRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public Optional<String> saveBoarding(BoardingDomain boarding) {


        return Optional.absent();
    }

    public boolean updatePaidStatus(String boardingId) {
        return true;
    }

    public boolean markAsRelease(String boardingId) {
        return true;
    }

    public boolean markAsOverdue(String boardingId) {
        return true;
    }

    public boolean markAsActive(String boardingId) {
        return true;
    }

    public void deleteById(String boardingId) {
    }


}
