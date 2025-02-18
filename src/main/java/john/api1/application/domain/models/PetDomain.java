package john.api1.application.domain.models;

import lombok.Data;

import java.util.List;


@Data
public class PetDomain {
    private String id;
    private String petName;
    private String animalType;
    private String breed;
    private String size;
    private double weight;
    private List<String> medicalsId;
    private String specialDescription;
    private String profilePictureUrl;
}
