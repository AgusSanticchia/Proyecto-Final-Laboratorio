package ar.edu.utn.frbb.tup.controller.dto;

public class MovimientosDto {
    private Double monto;
    private String tipoMoneda;
    private long numeroCuenta;

    public MovimientosDto(Double monto, long numeroCuenta, String tipoMoneda) {
        this.monto = monto;
        this.numeroCuenta = numeroCuenta;
        this.tipoMoneda = tipoMoneda;
    }
    public Double getMonto() {
        return monto;
    }
    public String getTipoMoneda() {
        return tipoMoneda;
    }
    public long getNumeroCuenta() {
        return numeroCuenta;
    }
}