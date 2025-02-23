package john.api1.application.ports.contracts;

public interface DeleteService<ID> {
    boolean deleteEntityByID(ID entityId);
}
