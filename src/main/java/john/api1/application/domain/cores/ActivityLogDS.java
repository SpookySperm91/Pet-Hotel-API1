package john.api1.application.domain.cores;

import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.cores.boarding.BoardingExtensionDS;
import john.api1.application.domain.cores.boarding.BoardingPricingDS;
import john.api1.application.domain.models.ActivityLogDomain;
import john.api1.application.domain.models.boarding.BoardingDomain;
import john.api1.application.dto.mapper.history.*;
import john.api1.application.ports.repositories.boarding.PricingCQRS;
import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
import john.api1.application.ports.repositories.pet.PetCQRS;
import john.api1.application.ports.repositories.request.ExtensionCQRS;
import john.api1.application.ports.repositories.request.GroomingCQRS;

import java.time.Instant;

public class ActivityLogDS {

    // Owner Register
    public static ActivityLogOwnerRegisterDTO transformRegisterOwner(ActivityLogDomain domain, ActivityLogDataContext context) {
        if (context.getOwner() != null) {
            PetOwnerCQRS owner = context.getOwner();

            String id = domain.getId();
            String activityType = domain.getActivityType().getActivityLogTypeToDTO();
            String description = domain.getDescription();
            String performBy = domain.getPerformedBy();
            Instant timestamp = domain.getTimestamp();

            // owner
            String ownerName = owner.ownerName();
            String ownerEmail = owner.ownerEmail();
            String ownerPhoneNumber = owner.ownerPhoneNumber();
            String address = ClientCreationDS.mapAddress(owner);

            return new ActivityLogOwnerRegisterDTO(
                    id, activityType, description, performBy, timestamp,
                    ownerName, ownerEmail, ownerPhoneNumber, address);
        }
        return null;
    }

    // Pet Register
    public static ActivityLogPetRegisterDTO transformRegisterPet(ActivityLogDomain domain, ActivityLogDataContext context) {
        if (context.getOwner() != null && context.getPet() != null) {
            PetOwnerCQRS owner = context.getOwner();
            PetCQRS pet = context.getPet();

            String id = domain.getId();
            String activityType = domain.getActivityType().getActivityLogTypeToDTO();
            String description = domain.getDescription();
            String performBy = domain.getPerformedBy();
            Instant timestamp = domain.getTimestamp();

            // Pet details
            String petName = pet.petName();
            String petType = pet.animalType();
            String petBreed = pet.breed();
            String petSize = pet.size();

            // Owner
            String ownerName = owner.ownerName();

            return new ActivityLogPetRegisterDTO(
                    id, activityType, description, performBy, timestamp,
                    petName, petType, petBreed, petSize, ownerName);
        }
        return null;
    }

    // Boarding
    public static ActivityLogBoardingDTO transformBoarding(ActivityLogDomain domain, ActivityLogDataContext context) {
        if (context.getBoarding() != null && context.getPricing() != null && context.getPet() != null) {
            BoardingDomain boarding = context.getBoarding();
            PricingCQRS pricing = context.getPricing();
            PetCQRS pet = context.getPet();

            String id = domain.getId();
            String activityType = domain.getActivityType().getActivityLogTypeToDTO();
            String description = domain.getDescription();
            String performBy = domain.getPerformedBy();
            Instant timestamp = domain.getTimestamp();

            // pet information
            String petName = pet.petName();
            String petType = pet.animalType();
            String breed = pet.breed();
            String size = pet.size();

            // owner
            String owner = domain.getPetOwner();

            // boarding details
            String boardingType = boarding.getBoardingType().getDurationType();
            int duration = (int) boarding.determineDuration();
            Double price = BoardingPricingDS.getBoardingTotal(pricing);
            Instant start = boarding.getBoardingStart();
            Instant end = boarding.getBoardingEnd();

            return new ActivityLogBoardingDTO(
                    id, activityType, description, performBy, timestamp,
                    petName, petType, breed, size,
                    owner,
                    boardingType, duration, price, start, end);
        }
        return null;
    }

    // Media Request
    public static ActivityLogRequestDTO transformRequestMedia(ActivityLogDomain domain, ActivityLogDataContext context) {
        if (context.getPet() == null) throw new PersistenceException("PetCQRS in context object is null");
        PetCQRS pet = context.getPet();

        String id = domain.getId();
        String activityType = domain.getActivityType().getActivityLogTypeToDTO();
        String requestType = domain.getRequestType() != null ? domain.getRequestType().getRequestType() : null;
        String description = domain.getDescription();
        String performBy = domain.getPerformedBy();
        Instant timestamp = domain.getTimestamp();

        // pet information
        String petName = pet.petName();
        String petType = pet.animalType();
        String breed = pet.breed();
        String size = pet.size();

        // owner
        String owner = domain.getPetOwner();


        return new ActivityLogRequestDTO(
                id, activityType, requestType, description, performBy, timestamp,
                petName, petType, breed, size,
                owner);

    }

    // Extension Request
    public static ActivityLogExtensionRequestDTO transformRequestExtension(ActivityLogDomain domain, ActivityLogDataContext context) {
        if (context.getBoarding() != null && context.getPricing() != null && context.getPet() != null && context.getExtension() != null) {
            BoardingDomain boarding = context.getBoarding();
            PricingCQRS pricing = context.getPricing();
            PetCQRS pet = context.getPet();
            ExtensionCQRS extension = context.getExtension();

            String id = domain.getId();
            String activityType = domain.getActivityType().getActivityLogTypeToDTO();
            String requestType = domain.getRequestType() != null ? domain.getRequestType().getRequestType() : null;
            String description = domain.getDescription();
            String performBy = domain.getPerformedBy();
            Instant timestamp = domain.getTimestamp();

            // pet information
            String petName = pet.petName();
            String petType = pet.animalType();
            String breed = pet.breed();
            String size = pet.size();

            // owner
            String owner = domain.getPetOwner();

            // extension details
            long duration = BoardingExtensionDS.determineDuration(extension);
            String durationType = extension.durationType().getDurationType();
            Instant current = boarding.getBoardingStart();
            Instant end = BoardingExtensionDS.calculateFinalBoardingEnd(boarding.getBoardingEnd(), extension);
            Double price = BoardingPricingDS.getBoardingTotal(pricing);

            return new ActivityLogExtensionRequestDTO(
                    id, activityType, requestType, description, performBy, timestamp,
                    petName, petType, breed, size,
                    owner,
                    duration, durationType, current, end, price);
        }
        return null;
    }

    // Grooming request
    public static ActivityLogGroomingRequestDTO transformRequestGrooming(ActivityLogDomain domain, ActivityLogDataContext context) {
        if (context.getBoarding() != null && context.getPricing() != null && context.getPet() != null && context.getGrooming() != null) {
            PetCQRS pet = context.getPet();
            GroomingCQRS grooming = context.getGrooming();


            String id = domain.getId();
            String activityType = domain.getActivityType().getActivityLogTypeToDTO();
            String requestType = domain.getRequestType() != null ? domain.getRequestType().getRequestType() : null;
            String description = domain.getDescription();
            String performBy = domain.getPerformedBy();
            Instant timestamp = domain.getTimestamp();

            // pet information
            String petName = pet.petName();
            String petType = pet.animalType();
            String breed = pet.breed();
            String size = pet.size();

            // owner
            String owner = domain.getPetOwner();

            // grooming details
            String groomingType = grooming.groomingType().getGroomingType();
            Double price = grooming.price();

            return new ActivityLogGroomingRequestDTO(
                    id, activityType, requestType, description, performBy, timestamp,
                    petName, petType, breed, size,
                    owner,
                    groomingType, price);
        }

        return null;
    }
}