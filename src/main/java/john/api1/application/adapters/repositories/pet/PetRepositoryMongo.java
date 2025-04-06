package john.api1.application.adapters.repositories.pet;

import com.mongodb.client.result.UpdateResult;
import john.api1.application.adapters.repositories.PetEntity;
import john.api1.application.components.exception.DomainArgumentException;
import john.api1.application.components.exception.PersistenceException;
import john.api1.application.domain.models.PetDomain;
import john.api1.application.ports.repositories.pet.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public class PetRepositoryMongo implements IPetCreateRepository, IPetSearchRepository, IPetUpdateRepository, IPetCQRSRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PetRepositoryMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    //
    // CREATE METHOD
    //
    @Override
    public Optional<String> createNewPet(PetDomain pet) {
        if (!ObjectId.isValid(pet.getOwnerId())) {
            throw new DomainArgumentException("Invalid Owner ID format");
        }

        PetEntity petEntity = new PetEntity(
                null,
                new ObjectId(pet.getOwnerId()),
                pet.getPetName(),
                pet.getAnimalType(),
                pet.getBreed(),
                pet.getSize(),
                pet.getAge(),
                pet.getSpecialDescription(),
                pet.getProfilePictureUrl(),
                Instant.now(),
                Instant.now(),
                pet.isBoarding()
        );

        PetEntity savedPet = mongoTemplate.save(petEntity);
        return Optional.ofNullable(savedPet.getId()).map(ObjectId::toString);
    }

    //
    // SEARCH METHODS
    //
    @Override
    public Optional<PetDomain> getPetById(String petId) {
        if (!ObjectId.isValid(petId)) return Optional.empty(); // Invalid ObjectId check

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petId)));
        PetEntity petEntity = mongoTemplate.findOne(query, PetEntity.class);

        return Optional.ofNullable(petEntity).map(this::mapToDomain);
    }

    private PetDomain mapToDomain(PetEntity petEntity) {
        return new PetDomain(
                petEntity.getId().toString(),
                petEntity.getPetOwnerId().toString(),
                petEntity.getPetName(),
                petEntity.getAnimalType(),
                petEntity.getBreed(),
                petEntity.getSize(),
                petEntity.getAge(),
                petEntity.getSpecialDescription(),
                petEntity.getProfilePictureUrl(),
                petEntity.getCreatedAt(),
                petEntity.getUpdatedAt(),
                petEntity.isBoarding()
        );
    }

    @Override
    public boolean existsById(String petId) {
        if (!ObjectId.isValid(petId)) return false;
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(petId)));
        return mongoTemplate.exists(query, PetEntity.class);
    }

    // Check if boarding or not
    public boolean isPetBoarding(String petId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(petId)));
        query.fields().include("boarding");

        PetEntity pet = mongoTemplate.findOne(query, PetEntity.class, "pets");
        return pet.isBoarding();
    }

    // List
    @Override
    public List<String> getAllPetsIdByOwner(String petOwnerId) {
        Query query = new Query(Criteria.where("ownerId").is(petOwnerId));
        query.fields().include("_id"); // Fetch only _id
        return mongoTemplate.find(query, PetEntity.class).stream()
                .map(pet -> pet.getId().toString()) // Ensure conversion to String
                .toList();
    }

    @Override
    public List<String> getAllPetsIdByAnimalType(String animalType) {
        Query query = new Query(Criteria.where("animalType").is(animalType));
        query.fields().include("_id");
        return mongoTemplate.find(query, PetEntity.class).stream()
                .map(pet -> pet.getId().toString()) // Convert to String
                .toList();
    }

    @Override
    public List<String> getAllPetsId() {
        Query query = new Query();
        query.fields().include("_id");
        return mongoTemplate.find(query, PetEntity.class).stream()
                .map(pet -> pet.getId().toString()) // Convert to String
                .toList();
    }

    //
    // UPDATE METHODS
    //
    @Override
    public boolean updatePet(PetDomain pet) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(pet.getId())));
        Update update = new Update()
                .set("petName", pet.getPetName())
                .set("animalType", pet.getAnimalType()) // Fixed: was 'type'
                .set("breed", pet.getBreed())
                .set("size", pet.getSize())
                .set("specialDescription", pet.getSpecialDescription())
                .set("profilePictureUrl", pet.getProfilePictureUrl()) // Fixed: was 'profilePicUrl'
                .set("updatedAt", Instant.now());

        UpdateResult result = mongoTemplate.updateFirst(query, update, PetEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new PersistenceException("Pet with ID " + pet.getId() + " not found.");
        }
        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean updatePetName(String petId, String newPetName) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(petId))); // Convert String to ObjectId
        Update update = new Update().set("petName", newPetName).set("updatedAt", Instant.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, PetEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new PersistenceException("Pet with ID " + petId + " not found.");
        }
        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean updatePetTypeAndBreed(String petId, String animalType, String breed) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(petId)));
        Update update = new Update()
                .set("animalType", animalType)
                .set("breed", breed)
                .set("updatedAt", Instant.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, PetEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new PersistenceException("Pet with ID " + petId + " not found.");
        }
        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean updatePetProfilePicture(String petId, String profilePictureUrl) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(petId)));
        Update update = new Update().set("profilePictureUrl", profilePictureUrl).set("updatedAt", Instant.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, PetEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new PersistenceException("Pet with ID " + petId + " not found.");
        }
        return result.getModifiedCount() > 0;
    }

    @Override
    public boolean updatePetStatus(String petId, boolean status) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(petId)));
        Update update = new Update().set("boarding", status).set("updatedAt", Instant.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, PetEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new PersistenceException("Pet with ID " + petId + " not found.");
        }
        return result.getModifiedCount() > 0;
    }

    @Override
    public Optional<PetCQRS> updatePetStatusResponse(String petId, boolean status) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(petId)));
        Update update = new Update().set("boarding", status).set("updatedAt", Instant.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, PetEntity.class);

        if (result.getMatchedCount() == 0) {
            throw new PersistenceException("Pet with ID " + petId + " not found.");
        }

        return Optional.ofNullable(mongoTemplate.findOne(query, PetEntity.class))
                .map(entity -> new PetCQRS(
                        entity.getPetName(),
                        entity.getAnimalType(),
                        entity.getBreed(),
                        entity.getSize(),
                        entity.getAge(),
                        entity.getSpecialDescription(),
                        entity.isBoarding()
                ));
    }


    // CQRS METHODS
    @Override
    public Optional<PetCQRS> getPetDetails(String id) {
        if (!ObjectId.isValid(id)) {
            throw new DomainArgumentException("Invalid Owner ID format");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));

        // Include all fields in PetCQRS
        query.fields().include(
                "petName",
                "ownerName",
                "animalType",
                "breed",
                "size",
                "age",
                "specialDescription",
                "boarding");

        return Optional.ofNullable(mongoTemplate.findOne(query, PetEntity.class))
                .map(entity -> new PetCQRS(
                        entity.getPetName(),
                        entity.getAnimalType(),
                        entity.getBreed(),
                        entity.getSize(),
                        entity.getAge(),
                        entity.getSpecialDescription(),
                        entity.isBoarding()
                ));
    }

    @Override
    public Optional<PetCQRS> getPetNameBreedType(String id) {
        if (!ObjectId.isValid(id)) {
            throw new DomainArgumentException("Invalid Owner ID format");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(new ObjectId(id)));

        // Include all fields in PetCQRS
        query.fields().include(
                "petName",
                "animalType",
                "breed");

        return Optional.ofNullable(mongoTemplate.findOne(query, PetEntity.class))
                .map(entity -> PetCQRS.mapNameTypeBreed(
                        entity.getPetName(),
                        entity.getAnimalType(),
                        entity.getBreed()
                ));
    }
}
