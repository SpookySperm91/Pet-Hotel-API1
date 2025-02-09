package john.api1.application.domain.models;

import john.api1.application.components.enums.Status;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.sql.Date;
import java.util.List;

    @Data
    public class ClientEntity {
        @Id
        private ObjectId id;
        private String referenceCode;
        private String email;
        private String phoneNumber;
        private String clientName;
        private String address;
        private List<ObjectId> animalIds;
        private Date createAt;
        private Date updateAt;
        private Status status;
    }
