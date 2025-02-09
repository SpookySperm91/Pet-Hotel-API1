package john.api1.application.adapters.persistence;

import john.api1.application.domain.models.AnimalEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Document(collection = "animals")
public interface AnimalRepository extends MongoRepository<AnimalEntity, ObjectId> {
}
