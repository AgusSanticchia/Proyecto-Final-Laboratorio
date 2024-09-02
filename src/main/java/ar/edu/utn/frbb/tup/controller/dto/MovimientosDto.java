package ar.edu.utn.frbb.tup.controller.dto;

public class MovimientosDto {
    private Double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private String moneda;
    private String tipoOperacion;

    public MovimientosDto(Double monto, long cuentaOrigen, long cuentaDestino, String moneda, String tipoOperacion) {
        this.monto = monto;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.moneda = moneda;
        this.tipoOperacion = tipoOperacion;
    }

    public String getMoneda() {
        return moneda;
    }

    public Double getMonto() {
        return monto;
    }

    public long getCuentaOrigen() {
        return cuentaOrigen;
    }

    public long getCuentaDestino() {return cuentaDestino;}

    public String getTipoOperacion() {return tipoOperacion;}

    public void setTipoOperacion(String tipoOperacion) {this.tipoOperacion = tipoOperacion;}
}
