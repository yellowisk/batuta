package br.unicamp.ic.mc322.exception;

public class SaveWriteException extends RuntimeException {
    public SaveWriteException(String message) {
        super(message);
    }
        public SaveWriteException (String message, Throwable cause){
        super(message, cause);
    }

}
