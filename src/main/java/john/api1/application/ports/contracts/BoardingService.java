package john.api1.application.ports.contracts;

import java.util.List;

public interface BoardingService<Animal, Boarding> {
    boolean assignAnimalToBoarding(Animal animal, Boarding room);
    boolean updateBoarding(Animal animal, Boarding room);
    List<Boarding> getAllBoardingRoomStatus();
}
