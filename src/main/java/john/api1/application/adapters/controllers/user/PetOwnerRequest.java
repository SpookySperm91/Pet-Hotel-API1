package john.api1.application.adapters.controllers.user;

import john.api1.application.components.DomainResponse;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.request.RequestExtensionCreatedDTO;
import john.api1.application.dto.mapper.request.RequestGroomingCreatedDTO;
import john.api1.application.dto.mapper.request.RequestMediaCreatedDTO;
import john.api1.application.dto.request.request.RequestExtensionRDTO;
import john.api1.application.dto.request.request.RequestGroomingRDTO;
import john.api1.application.dto.request.request.RequestMediaRDTO;
import john.api1.application.ports.services.request.IRequestCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/boarding/request/")
public class PetOwnerRequest {
    private final IRequestCreate requestCreate;

    @Autowired
    public PetOwnerRequest(IRequestCreate requestCreate) {
        this.requestCreate = requestCreate;
    }

    @PostMapping("media")
    public ResponseEntity<DTOResponse<RequestMediaCreatedDTO>> requestMedia(
            @Valid @RequestBody RequestMediaRDTO request,
            BindingResult result) {

        return handleRequest(request, result, (Object req) -> requestCreate.createRequestMedia((RequestMediaRDTO) req));
    }


    @PostMapping("extension")
    public ResponseEntity<DTOResponse<RequestExtensionCreatedDTO>> requestExtension(
            @Valid @RequestBody RequestExtensionRDTO request,
            BindingResult result) {

        return handleRequest(request, result, (Object req) -> requestCreate.createRequestExtension((RequestExtensionRDTO) req));
    }


    @PostMapping("grooming")
    public ResponseEntity<DTOResponse<RequestGroomingCreatedDTO>> requestGrooming(
            @Valid @RequestBody RequestGroomingRDTO request,
            BindingResult result) {

        return handleRequest(request, result, (Object req) -> requestCreate.createRequestGrooming((RequestGroomingRDTO) req));
    }


    private <T> ResponseEntity<DTOResponse<T>> handleRequest(Object request, BindingResult result,
                                                             Function<Object, DomainResponse<T>> createRequest) {
        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }

        var domainResponse = createRequest.apply(request);
        if (!domainResponse.isSuccess()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, domainResponse.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DTOResponse.of(
                        HttpStatus.CREATED.value(),
                        domainResponse.getData(),
                        domainResponse.getMessage()));
    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}