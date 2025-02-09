package john.api1.application.domain.ports.contracts;

public interface DeleteService<ID> {
    boolean deleteEntityByID(ID entityId);
}
