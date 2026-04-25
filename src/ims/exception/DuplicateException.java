package ims.exception;

/** Thrown when a duplicate record is detected (e.g. same SKU, same email). */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) { super(message); }
}
