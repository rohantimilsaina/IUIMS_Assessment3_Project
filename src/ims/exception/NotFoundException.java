package ims.exception;

/** Thrown when a record is not found by ID or name. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, int id) {
        super(entity + " with ID " + id + " not found.");
    }
    public NotFoundException(String message) {
        super(message);
    }
}
