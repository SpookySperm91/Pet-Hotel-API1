//package john.api1.application.dto.mapper.boarding;
//
//import john.api1.application.domain.models.boarding.BoardingDomain;
//import john.api1.application.domain.models.boarding.BoardingPricingDomain;
//import john.api1.application.ports.repositories.owner.PetOwnerCQRS;
//import john.api1.application.ports.repositories.pet.PetCQRS;
//import john.api1.application.ports.repositories.wrapper.MediaIdUrlExpire;
//
//import java.time.Instant;
//
//public record BoardingDTOs(
//        // O(1)
//        String id,
//        String petId,
//        String ownerId,
//
//        // Pet
//        String photoId,
//        String photoUrl,
//        Instant expiredAt,
//        String petName,
//        String petType,
//        String petBreed,
//        String petSize,
//        int age,
//
//        // Owner
//        String ownerName,
//        String ownerEmail,
//        String ownerPhoneNumber,
//        String ownerAddress,
//
//        // Boarding details
//        String boardingType,
//        Instant boardingStart,
//        Instant boardingEnd,
//        String paymentStatus,
//        String notes,
//
//        // Pricing breakdown
//        double rate,
//        double boardingPrice,
//        double total,
//
//        // Created at
//        Instant createdAt
//)  {
//
//    public static BoardingDTOs map(BoardingDomain boarding,
//                                   BoardingPricingDomain pricing,
//                                   PetOwnerCQRS ownerCQRS,
//                                   PetCQRS petCQRS,
//                                   MediaIdUrlExpire media) {
//
//        return new BoardingDTOs(
//                boarding.getId(),
//                boarding.getPetId(),
//                boarding.getOwnerId(),
//
//                media.id(),
//                media.mediaUrl(),
//                media.expireAt(),
//                petCQRS.petName(),
//                petCQRS.animalType(),
//                petCQRS.breed(),
//                petCQRS.size(),
//                petCQRS.age(),
//
//                ownerCQRS.ownerName(),
//                ownerCQRS.ownerEmail(),
//                ownerCQRS.ownerPhoneNumber(),
//                String.format("%s, %s, %s", ownerCQRS.streetAddress(), ownerCQRS.cityAddress(), ownerCQRS.stateAddress()),
//
//                boarding.getBoardingType().getDurationType(),
//                boarding.getBoardingStart(),
//                boarding.getBoardingEnd(),
//                boarding.getPaymentStatus().getPaymentStatus(),
//                boarding.getNotes(),
//                )
//
//
//    }
//
//}
