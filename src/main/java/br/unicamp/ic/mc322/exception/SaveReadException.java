package br.unicamp.ic.mc322.exception;

public class SaveReadException extends RuntimeException {
    public SaveReadException(String message) {
        super(message);
    }
    public SaveReadException (String message, Throwable cause){
        super(message, cause);
    }
}
