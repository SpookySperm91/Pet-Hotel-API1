package john.api1.application.ports.repositories;

public interface IUpdateRepository<T, ID> {
    T updateById(ID id);
}
