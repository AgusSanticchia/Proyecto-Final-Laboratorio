package ar.edu.utn.frbb.tup.model;

import java.time.LocalDateTime;
import java.util.Objects;

import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoOperacion;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;

public class Movimientos {
    private double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private LocalDateTime fecha;
    private TipoMoneda tipoMoneda;
    private TipoOperacion tipoOperacion;
    private String descripcion;


    public Movimientos(MovimientosDto movimientosDto) {
        this.monto = movimientosDto.getMonto();
        this.cuentaOrigen = movimientosDto.getNumeroCuenta();
        this.tipoMoneda = TipoMoneda.fromString(movimientosDto.getTipoMoneda());
        this.fecha = LocalDateTime.now();
    }

    public Movimientos(MovimientosTransferenciasDto movimientosTransferenciasDto) {
        this.cuentaDestino = movimientosTransferenciasDto.getCuentaDestino();
        this.cuentaOrigen = movimientosTransferenciasDto.getCuentaOrigen();
        this.fecha = LocalDateTime.now();
        this.monto = movimientosTransferenciasDto.getMonto();
        this.tipoMoneda = TipoMoneda.fromString(movimientosTransferenciasDto.getTipoMoneda());
        this.descripcion = movimientosTransferenciasDto.getDescripcion();
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
    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }
    public void setTipoOperacion(TipoOperacion tipoOperacion) {this.tipoOperacion = tipoOperacion;}
    public LocalDateTime getFecha() {
        return fecha;
    }
    public String getDescripcion() {return descripcion;}

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