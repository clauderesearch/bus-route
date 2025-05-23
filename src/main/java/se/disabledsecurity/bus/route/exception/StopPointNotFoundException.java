package se.disabledsecurity.bus.route.exception;

public class StopPointNotFoundException extends RuntimeException {
    public StopPointNotFoundException(String message) {
        super(message);
    }
    
    public StopPointNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}