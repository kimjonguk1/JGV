package dev.jwkim.jgv.exceptions;

public class TransactionalException extends RuntimeException {
    public TransactionalException() {
        super();
    }

    public TransactionalException(String message) {
        super(message);
    }

    public TransactionalException(String message, Throwable cause) {
        super(message, cause);
    }


    public TransactionalException(Throwable cause) {
        super(cause);
    }
}
