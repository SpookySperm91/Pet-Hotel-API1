package john.api1.application.ports.contracts;

public interface MediaStorageService<T> {
    boolean uploadMedia(T media);
    byte[] retrieveMedia(T media);
    boolean deleteMedia(T media);
}
