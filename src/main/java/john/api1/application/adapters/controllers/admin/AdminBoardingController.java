package john.api1.application.adapters.controllers.admin;

import john.api1.application.components.enums.boarding.BoardingType;
import john.api1.application.components.enums.boarding.PaymentStatus;
import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.boarding.BoardingCreatedDTO;
import john.api1.application.dto.mapper.boarding.BoardingReleasedDTO;
import john.api1.application.dto.request.BoardingRDTO;
import john.api1.application.ports.services.boarding.IBoardingCreate;
import john.api1.application.ports.services.boarding.IBoardingManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.ZoneId;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/boarding")
public class AdminBoardingController {
    private static final Logger logger = LoggerFactory.getLogger(AdminBoardingController.class);
    private final IBoardingCreate boardingCreate;
    private final IBoardingManagement boardingManagement;

    @Autowired
    public AdminBoardingController(IBoardingCreate boardingCreate, IBoardingManagement boardingManagement) {
        this.boardingCreate = boardingCreate;
        this.boardingManagement = boardingManagement;
    }

    // CREATE BOARDING
    // Check Request values if valid
    // Convert String and Times to Enum and Instant
    // Initiate boarding creation
    // Return response
    @PostMapping("/create")
    public ResponseEntity<DTOResponse<BoardingCreatedDTO>> createNewBoarding(
            @Valid @RequestBody BoardingRDTO boardingRequest,
            BindingResult result) {
        try {
            // Check for errors
            if (result.hasErrors()) {
                String errorMessage = result.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(", "));
                return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
            }

            // Session and magic (later)
            ////////////////////////////
            ////////////////////////////

            // Start creating boarding
            BoardingType boardingType = BoardingType.fromStringOrError(boardingRequest.getBoardingType());
            PaymentStatus paymentStatus = PaymentStatus.fromStringOrError(boardingRequest.getPaymentStatus());
            Instant startAt = boardingRequest.getBoardingStart().atZone(ZoneId.systemDefault()).toInstant();
            Instant endAt = boardingRequest.getBoardingEnd().atZone(ZoneId.systemDefault()).toInstant();
            var boardNew = boardingCreate.createNewBoarding(boardingRequest, boardingType, paymentStatus, startAt, endAt);

            // Response
            if (!boardNew.isSuccess()) {
                return buildErrorResponse(HttpStatus.BAD_REQUEST, boardNew.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK)
                    .body(DTOResponse.of(
                            HttpStatus.OK.value(),
                            boardNew.getData(),
                            boardNew.getMessage()));

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during boarding creation", e);
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error when processing request");
        }
    }

    @PutMapping("/release/{boardingId}")
    public ResponseEntity<DTOResponse<BoardingReleasedDTO>> releaseBoarding(@Valid @PathVariable String boardingId) {
        if (boardingId == null || boardingId.trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Boarding ID cannot be null, empty, or blank!");
        }

        // Session and magic (later)
        ////////////////////////////
        ////////////////////////////

        // release
        var release = boardingManagement.releasedBoarding(boardingId);
        if (!release.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, release.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        release.getData(),
                        release.getMessage()));

    }


    @PutMapping("/release-force/{boardingId}")
    public ResponseEntity<DTOResponse<BoardingReleasedDTO>> forceReleaseBoarding(@Valid @PathVariable String boardingId) {
        if (boardingId == null || boardingId.trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Boarding ID cannot be null, empty, or blank!");
        }

        // Session and magic (later)
        ////////////////////////////
        ////////////////////////////

        // release
        var release = boardingManagement.forceReleasedBoarding(boardingId);
        if (!release.isSuccess()) return buildErrorResponse(HttpStatus.BAD_REQUEST, release.getMessage());

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(
                        HttpStatus.OK.value(),
                        release.getData(),
                        release.getMessage()));

    }


    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}
