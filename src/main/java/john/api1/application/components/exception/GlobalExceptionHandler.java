package john.api1.application.components.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice  // Handles exceptions globally
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errors", errors));
    }

    @ExceptionHandler(XSSDetectedException.class)
    public ResponseEntity<Map<String, String>> handleXSSDetectedException(XSSDetectedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));  // Send the error message in the response body
    }

    @ExceptionHandler(SessionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidSession(SessionException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }

}
