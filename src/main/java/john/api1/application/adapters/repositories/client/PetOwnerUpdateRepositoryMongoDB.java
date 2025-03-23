package john.api1.application.adapters.repositories.client;

import com.mongodb.client.result.UpdateResult;
import john.api1.application.adapters.repositories.ClientEntity;
import john.api1.application.ports.repositories.account.IPetOwnerUpdateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public class PetOwnerUpdateRepositoryMongoDB implements IPetOwnerUpdateRepository {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PetOwnerUpdateRepositoryMongoDB(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    // Pet related
    @Override
    public boolean addNewPet(String petOwnerId, String petId) {
        if (!checkIfValid(petOwnerId) || !checkIfValid(petId)) return false;

        Query existsQuery = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        if (!mongoTemplate.exists(existsQuery, ClientEntity.class)) return false; // Ensure owner exists

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .addToSet("animalIds", new ObjectId(petId))
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }


    @Override
    public boolean removePet(String petOwnerId, String petId) {
        if (!checkIfValid(petOwnerId) || !checkIfValid(petId)) return false;

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .pull("animalIds", new ObjectId(petId))
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }


    // Non important
    @Override
    public boolean updateFullName(String petOwnerId, String fullName) {
        if (!checkIfValid(petOwnerId)) return false;

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .set("clientName", fullName)
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }

    @Override
    public boolean updateStreetAddress(String petOwnerId, String streetAddress) {
        if (!checkIfValid(petOwnerId)) return false;

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .set("streetAddress", streetAddress)
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }

    @Override
    public boolean updateCityAddress(String petOwnerId, String cityAddress) {
        if (!checkIfValid(petOwnerId)) return false;

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .set("cityAddress", cityAddress)
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }

    @Override
    public boolean updateStateAddress(String petOwnerId, String stateAddress) {
        if (!checkIfValid(petOwnerId)) return false;

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .set("stateAddress", stateAddress)
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }

    @Override
    public boolean updateEmergencyPhoneNumber(String petOwnerId, String phoneNumber) {
        if (!checkIfValid(petOwnerId)) return false;

        Query query = new Query(Criteria.where("_id").is(new ObjectId(petOwnerId)));
        Update update = new Update()
                .set("emergencyNumber", phoneNumber)
                .set("updateAt", Instant.now());

        return updateField(query, update);
    }

    private boolean checkIfValid(String id) {
        return ObjectId.isValid(id);
    }


    private boolean updateField(Query query, Update update) {
        UpdateResult result = mongoTemplate.updateFirst(query, update, ClientEntity.class);
        if (result.getMatchedCount() == 0) {
            System.out.println("No matching document found for update.");
            return false;
        }
        return result.getModifiedCount() > 0;
    }

}
