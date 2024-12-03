package ar.edu.utn.frbb.tup.exception.clientes;

public class ClienteAlreadyExistsException extends Exception {
    public ClienteAlreadyExistsException(String message) {
        super(message);
    }
}
