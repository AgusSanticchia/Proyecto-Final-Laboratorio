package ar.edu.utn.frbb.tup.model.exception;

public class TipoMonedaNoCoincidenException extends RuntimeException {
    public TipoMonedaNoCoincidenException(String message) {
        super(message);
    }
}
