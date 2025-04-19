package john.api1.application.domain.cores;

import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.domain.models.request.RequestDomain;
import john.api1.application.ports.repositories.boarding.PricingCQRS;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.request.ExtensionCQRS;
import john.api1.application.ports.repositories.request.GroomingCQRS;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class ActivityLogDataContext {
    private final BoardingDomain boarding;
    private final RequestDomain request;
    private final PricingCQRS pricing;
    private final PetCQRS pet;
    private final PetOwnerCQRS owner;
    private final ExtensionCQRS extension;
    private final GroomingCQRS grooming;

    @Setter
    public static class Builder {
        private BoardingDomain boarding;
        private RequestDomain request;
        private PricingCQRS pricing;
        private PetCQRS pet;
        private PetOwnerCQRS owner;
        private ExtensionCQRS extension;
        private GroomingCQRS grooming;


        public Builder boarding(BoardingDomain boarding) {
            this.boarding = boarding;
            return this;
        }

        public Builder request(RequestDomain request) {
            this.request = request;
            return this;
        }

        public Builder pricing(PricingCQRS pricing) {
            this.pricing = pricing;
            return this;
        }

        public Builder pet(PetCQRS pet) {
            this.pet = pet;
            return this;
        }

        public Builder owner(PetOwnerCQRS owner) {
            this.owner = owner;
            return this;
        }

        public Builder extension(ExtensionCQRS extension) {
            this.extension = extension;
            return this;
        }

        public Builder grooming(GroomingCQRS grooming) {
            this.grooming = grooming;
            return this;
        }

        public ActivityLogDataContext build() {
            return new ActivityLogDataContext(boarding, request, pricing, pet, owner, extension, grooming);
        }
    }
}
