package ar.edu.utn.frbb.tup.model.exception.cuentas;

public class CuentaAlreadyExistsException extends Throwable{
    public CuentaAlreadyExistsException(String message) {
        super(message);
    }
}
