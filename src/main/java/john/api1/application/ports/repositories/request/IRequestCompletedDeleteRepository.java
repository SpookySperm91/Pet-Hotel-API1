package john.api1.application.ports.repositories.request;

public interface IRequestCompletedDeleteRepository {
    void deletePhotoByRequestId(String id);

    void deleteVideoByRequestId(String id);

    void deleteExtensionById(String id);

    void deleteGroomingById(String id);
}
