package john.api1.application.ports.repositories.boarding;

import com.google.common.base.Optional;
import john.api1.application.domain.models.boarding.BoardingDomain;

public interface IBoardingCreateRepository {
    String saveBoarding(BoardingDomain boarding);
}
