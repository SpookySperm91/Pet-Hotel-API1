package john.api1.application.domain.ports.persistence;

public interface IUpdateRepository<T, ID> {
    T updateEntity(T entity);
    T updateById(ID id);
}
