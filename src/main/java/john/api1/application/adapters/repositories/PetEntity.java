package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "${db.collection.pet}")
public class PetEntity {
    @Id
    private ObjectId id;
    private ObjectId petOwnerId;

    private String petName;
    private String animalType;
    private String breed;
    private String size;
    private int age;
    private String specialDescription;
    private String profilePictureUrl;

    private Instant createdAt;
    private Instant updatedAt;

    private boolean boarding;
}
