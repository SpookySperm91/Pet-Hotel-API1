package john.api1.application.adapters.controllers.admin;

import john.api1.application.dto.DTOResponse;
import john.api1.application.dto.mapper.boarding.BoardingDTO;
import john.api1.application.ports.services.boarding.IBoardingSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/search/boarding")
public class AdminBoardingSearchController {
    private static final Logger logger = LoggerFactory.getLogger(AdminBoardingController.class);
    private final IBoardingSearch boardingSearch;

    @Autowired
    public AdminBoardingSearchController(IBoardingSearch boardingSearch) {
        this.boardingSearch = boardingSearch;
    }

    @GetMapping("/all")
    public ResponseEntity<DTOResponse<List<BoardingDTO>>> getALlBoarding() {
        var search = boardingSearch.allBoarding();

        if (!search.isSuccess()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, search.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), search.getData(), search.getMessage()));
    }

    @GetMapping("/recent")
    public ResponseEntity<DTOResponse<BoardingDTO>> getRecentBoarding() {
        var search = boardingSearch.recentBoarding()    ;

        if (!search.isSuccess()) {
            return buildErrorResponse(
                    HttpStatus.BAD_REQUEST, search.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(DTOResponse.of(HttpStatus.OK.value(), search.getData(), search.getMessage()));
    }

    private <T> ResponseEntity<DTOResponse<T>> buildErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(DTOResponse.message(status.value(), message));
    }

}
