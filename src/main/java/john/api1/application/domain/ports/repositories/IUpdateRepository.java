package john.api1.application.domain.ports.repositories;

public interface IUpdateRepository<T, ID> {
    T updateById(ID id);
}
