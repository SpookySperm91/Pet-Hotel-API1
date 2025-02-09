package john.api1.application.domain.models.metadata;

import lombok.Data;

import java.sql.Date;

@Data
public class AnimalMetadata {
    private String animalType;
    private String animalName;
    private String breed;
    private Date birthDate;
    private boolean vaccinated;
    private String specialNeeds;
}
