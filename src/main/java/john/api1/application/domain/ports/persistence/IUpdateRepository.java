package john.api1.application.domain.ports.persistence;

public interface IUpdateRepository<T, ID> {
    T updateById(ID id);
}
