package john.api1.application.domain.ports.contracts;

import java.util.List;

public interface BoardingService<Animal, Boarding> {
    boolean assignAnimalToBoarding(Animal animal, Boarding room);
    boolean updateBoarding(Animal animal, Boarding room);
    List<Boarding> getAllBoardingRoomStatus();
}
