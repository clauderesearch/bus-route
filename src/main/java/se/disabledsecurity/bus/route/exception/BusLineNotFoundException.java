package se.disabledsecurity.bus.route.exception;

public class BusLineNotFoundException extends RuntimeException {
    public BusLineNotFoundException(String message) {
        super(message);
    }
    
    public BusLineNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}