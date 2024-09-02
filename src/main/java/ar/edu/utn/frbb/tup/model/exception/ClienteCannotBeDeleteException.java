package ar.edu.utn.frbb.tup.model.exception;

public class ClienteCannotBeDeleteException extends RuntimeException {
    public ClienteCannotBeDeleteException(String message) {
        super(message);
    }
}
