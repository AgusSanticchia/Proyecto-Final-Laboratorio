package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;

import java.time.LocalDateTime;

public class Movimientos {
    private TipoOperacion tipoOperacion;
    private Double monto;
    private String descripcion;
    private LocalDateTime fecha;
    private TipoMoneda tipoMoneda;
    private long cuentaOrigen;
    private long cuentaDestino;
    private long movimientoId;

    public Movimientos(TipoOperacion tipoOperacion, Double monto,String descripcion, LocalDateTime fecha, TipoMoneda tipoMoneda, long cuentaOrigen, long cuentaDestino, long movimientoId) {
        this.tipoOperacion = tipoOperacion;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.tipoMoneda = tipoMoneda;
        this.cuentaOrigen = cuentaOrigen;
        this.cuentaDestino = cuentaDestino;
        this.movimientoId = movimientoId;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
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

    public long getMovimientoId() {
        return movimientoId;
    }

    public void setMovimientoId(long movimientoId) {
        this.movimientoId = movimientoId;
    }

    @Override
    public String toString() {
        return "\n Tipo de Operacion: " + getTipoOperacion() +
                "\n Descripcion: " + getDescripcion() +
                "\n Id de la operacion: " + getMovimientoId() +
                "\n Monto: " + getMonto() +
                "\n Tipo de moneda: " + getTipoMoneda() +
                "\n Fecha: " + getFecha();
    }
}
