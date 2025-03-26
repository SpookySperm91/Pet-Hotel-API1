package john.api1.application.ports.services.boarding;

import john.api1.application.components.DomainResponse;

public interface IBoardingManagement {
    DomainResponse<String> releaseBoarding(); // release after completed
    DomainResponse<String> updatePaidStatus(); // after paid, or new pay service
    DomainResponse<String> markOverdue();
}
