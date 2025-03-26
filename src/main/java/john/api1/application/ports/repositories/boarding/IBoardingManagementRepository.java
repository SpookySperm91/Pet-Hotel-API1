package john.api1.application.ports.repositories.boarding;

public interface IBoardingManagementRepository {
    boolean updatePaidStatus(String boardingId);

    boolean markAsRelease(String boardingId);

    boolean markAsOverdue(String boardingId);

    boolean markAsActive(String boardingId);

    void deleteById(String boardingId);
}
