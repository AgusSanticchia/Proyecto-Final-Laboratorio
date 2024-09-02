package ar.edu.utn.frbb.tup.model.exception;

public class CuentaNotExistException extends RuntimeException {
    public CuentaNotExistException(String message) {
        super(message);
    }
}
