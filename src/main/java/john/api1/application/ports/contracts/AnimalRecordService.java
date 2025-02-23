package john.api1.application.ports.contracts;

import java.util.List;
import java.util.Optional;

public interface AnimalRecordService<Animal, Record, Owner> {
    boolean generateReport(Animal animal, Record report);
    Optional<List<Record>> getReportsByAnimal(Animal animal);             // get records for THAT animal
    Optional<List<Record>> getReportsForAllAnimalsOfClient(Owner owner);  // get records for all owner's animal
}
