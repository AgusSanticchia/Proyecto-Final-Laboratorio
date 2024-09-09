package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;

import java.time.LocalDateTime;
import java.util.Objects;

public class Movimientos {
    private double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private String tipoMoneda;
    TipoOperacion tipoOperacion;
    private LocalDateTime fecha;

    public Movimientos(MovimientosDto movimientosDto){
        this.fecha = LocalDateTime.now();
        this.monto = movimientosDto.getMonto();
        this.tipoMoneda = movimientosDto.getTipoMoneda();
        this.cuentaOrigen = movimientosDto.getNumeroCuenta();
    }

    public Movimientos(MovimientosTransferenciasDto movimientosTransferenciasDto) {
        this.cuentaDestino = movimientosTransferenciasDto.getNumeroCuentaOrigen();
        this.cuentaOrigen = movimientosTransferenciasDto.getNumeroCuentaDestino();
        this.fecha = LocalDateTime.now();
        this.monto = movimientosTransferenciasDto.getMonto();
        this.tipoMoneda = movimientosTransferenciasDto.getTipoMoneda();
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
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

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }


    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "\n Tipo de Operacion: " + getTipoOperacion() + "\n Monto: " + getMonto();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movimientos that = (Movimientos) o;
        return Objects.equals(tipoMoneda, that.tipoMoneda);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tipoMoneda);
    }
}
