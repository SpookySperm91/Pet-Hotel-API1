package john.api1.application.adapters.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pets")
public class PetEntity {
    @Id
    private ObjectId id;
    private ObjectId petOwnerId;

    private String petName;
    private String animalType;
    private String breed;
    private String size;
    private String specialDescription;
    private String profilePictureUrl;

    private Instant createAt;
    private Instant updateAt;


}
