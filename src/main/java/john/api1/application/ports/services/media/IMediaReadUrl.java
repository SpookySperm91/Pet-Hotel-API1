package john.api1.application.ports.services.media;

public interface IMediaReadUrl {
    String findById(String id);

    String findByOwnerId(String id);
}
