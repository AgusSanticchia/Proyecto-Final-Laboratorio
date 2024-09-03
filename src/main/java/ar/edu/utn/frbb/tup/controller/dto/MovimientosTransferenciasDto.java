package ar.edu.utn.frbb.tup.controller.dto;

public class MovimientosTransferenciasDto {
    private Double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private String tipoMoneda;

    public MovimientosTransferenciasDto(long cuentaDestino, long cuentaOrigen, Double monto, String tipoMoneda) {
        this.cuentaDestino = cuentaDestino;
        this.cuentaOrigen = cuentaOrigen;
        this.monto = monto;
        this.tipoMoneda = tipoMoneda;
    }

    public Double getMonto() {
        return monto;
    }

    public long getNumeroCuentaDestino() {
        return cuentaDestino;
    }

    public long getNumeroCuentaOrigen() {
        return cuentaOrigen;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }
}
