package john.api1.application.ports.services;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.request.RegisterRDTO;
import john.api1.application.services.response.RegisterResponse;

public interface IRegisterNewClient<T, Response> {
    DomainResponse<Response> registerNewClient(T request);
}
