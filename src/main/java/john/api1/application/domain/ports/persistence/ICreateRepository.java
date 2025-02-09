package john.api1.application.domain.ports.persistence;

public interface ICreateRepository<T> {
    T createEntity(T entity);
}
