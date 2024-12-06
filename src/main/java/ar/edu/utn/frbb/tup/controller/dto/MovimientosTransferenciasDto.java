package ar.edu.utn.frbb.tup.controller.dto;

public class MovimientosTransferenciasDto {
    private Double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private String tipoMoneda;
    private String descripcion;

    public MovimientosTransferenciasDto(long cuentaDestino, long cuentaOrigen, Double monto, String tipoMoneda, String descripcion) {
        this.cuentaDestino = cuentaDestino;
        this.cuentaOrigen = cuentaOrigen;
        this.monto = monto;
        this.tipoMoneda = tipoMoneda;
        this.descripcion = descripcion;
    }

    public Double getMonto() {
        return monto;
    }
    public long getCuentaDestino() {
        return cuentaDestino;
    }
    public long getCuentaOrigen() {
        return cuentaOrigen;
    }
    public String getTipoMoneda() {
        return tipoMoneda;
    }
    public String getDescripcion() { return descripcion; }
}