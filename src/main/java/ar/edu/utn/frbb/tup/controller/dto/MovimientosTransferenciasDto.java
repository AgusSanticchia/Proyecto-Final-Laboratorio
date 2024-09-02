package ar.edu.utn.frbb.tup.controller.dto;

public class MovimientosTransferenciasDto {
    private Double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private String moneda;
    private String tipoOperacion;
    private String idTransferencia;

    public MovimientosTransferenciasDto(Double monto, long cuentaOrigen, long cuentaDestino, String moneda, String tipoOperacion, String idTransferencia) {
        this.monto = monto;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.moneda = moneda;
        this.tipoOperacion = tipoOperacion;
        this.idTransferencia = idTransferencia;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public long getCuentaOrigen() {
        return cuentaOrigen;
    }

    public void setCuentaOrigen(long cuentaOrigen) {
        this.cuentaOrigen = cuentaOrigen;
    }

    public long getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(long cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getIdTransferencia() {
        return idTransferencia;
    }

    public void setIdTransferencia(String idTransferencia) {
        this.idTransferencia = idTransferencia;
    }
}
