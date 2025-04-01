package john.api1.application.components.enums;

import john.api1.application.components.exception.DomainArgumentException;

public enum SpeciesType {
    DOG, CAT;

    public static SpeciesType fromStringToSpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            throw new DomainArgumentException("Species type cannot be empty");
        }

        return switch (species.trim().toUpperCase()) {
            case "DOG" -> SpeciesType.DOG;
            case "CAT" -> SpeciesType.CAT;
            default -> throw new DomainArgumentException("Invalid species type: " + species);
        };
    }

}
