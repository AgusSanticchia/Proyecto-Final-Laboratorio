package ar.edu.utn.frbb.tup.model.exception;

public class CuentaNotFoundException extends RuntimeException {
  public CuentaNotFoundException(String message) {
    super(message);
  }
}
