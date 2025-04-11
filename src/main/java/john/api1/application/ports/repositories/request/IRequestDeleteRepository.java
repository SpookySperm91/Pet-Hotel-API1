package john.api1.application.ports.repositories.request;

import john.api1.application.adapters.repositories.RequestEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IRequestDeleteRepository extends MongoRepository<RequestEntity, ObjectId> {
}
