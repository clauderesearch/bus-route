package se.disabledsecurity.bus.route.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusLineNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleBusLineNotFound(BusLineNotFoundException ex) {
        Map<String, Object> errorResponse = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Bus Line Not Found",
            "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(StopPointNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStopPointNotFound(StopPointNotFoundException ex) {
        Map<String, Object> errorResponse = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Stop Point Not Found",
            "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ApiFetchException.class)
    public ResponseEntity<Map<String, Object>> handleApiFetchException(ApiFetchException ex) {
        Map<String, Object> errorResponse = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
            "error", "External API Error",
            "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Internal Server Error",
            "message", "An unexpected error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}