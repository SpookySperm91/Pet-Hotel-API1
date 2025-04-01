package john.api1.application.components.enums;

import john.api1.application.components.exception.DomainArgumentException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PetPrices {
    SMALL(SpeciesType.DOG, PetSize.SMALL, 25, 300, 500),
    MEDIUM(SpeciesType.DOG, PetSize.MEDIUM, 30, 400, 650),
    LARGE(SpeciesType.DOG, PetSize.LARGE, 40, 500, 800),
    XL(SpeciesType.DOG, PetSize.XL, 50, 600, 950),

    KITTEN(SpeciesType.CAT, PetSize.KITTEN, 15, 250, 400),
    ADULT_CAT(SpeciesType.CAT, PetSize.ADULT, 20, 350, 600);

    private final SpeciesType species;
    private final PetSize size;
    private final double boardingPrice;
    private final double basicGroomingPrice;
    private final double fullGroomingPrice;


    // Get Species and Size
    public static PetPrices fromSpeciesAndSize(SpeciesType species, PetSize size) {
        if ((species == SpeciesType.DOG && !size.isDogSize()) ||
                (species == SpeciesType.CAT && !size.isCatSize())) {
            throw new DomainArgumentException("Invalid pet size for species: " + species + " " + size);
        }

        return Arrays.stream(values())
                .filter(p -> p.species == species && p.size == size)
                .findFirst()
                .orElseThrow(() -> new DomainArgumentException("No pricing found for: " + species + " " + size));
    }

}



