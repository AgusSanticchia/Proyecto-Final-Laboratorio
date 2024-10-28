package ar.edu.utn.frbb.tup.model.exception.clientes;

public class ClienteAlreadyExistsException extends Throwable {
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
