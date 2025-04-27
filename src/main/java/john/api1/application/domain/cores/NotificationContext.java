package john.api1.application.domain.cores;

import john.api1.application.components.enums.NotificationType;
import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.RequestType;

import java.time.Instant;

public record NotificationContext(
        NotificationType notificationType,
        String ownerName,
        String petName,
        RequestType requestType,
        BoardingType boardingType,
        Instant newDuration,
        double charges,
        Instant checkoutTime
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private NotificationType notificationType;
        private String ownerName;
        private String petName;
        private RequestType requestType;
        private BoardingType boardingType;
        private Instant newDuration;
        private double charges;
        private Instant checkoutTime;

        public Builder notificationType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public Builder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Builder petName(String petName) {
            this.petName = petName;
            return this;
        }

        public Builder requestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder boardingType(BoardingType boardingType) {
            this.boardingType = boardingType;
            return this;
        }

        public Builder newDuration(Instant newDuration) {
            this.newDuration = newDuration;
            return this;
        }

        public Builder charges(double charges) {
            this.charges = charges;
            return this;
        }

        public Builder checkoutTime(Instant checkoutTime) {
            this.checkoutTime = checkoutTime;
            return this;
        }

        public NotificationContext build() {
            return new NotificationContext(
                    notificationType,
                    ownerName,
                    petName,
                    requestType,
                    boardingType,
                    newDuration,
                    charges,
                    checkoutTime
            );
        }
    }

}
