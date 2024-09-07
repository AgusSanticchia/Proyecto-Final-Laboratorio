package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.MovimientosDto;
import ar.edu.utn.frbb.tup.controller.dto.MovimientosTransferenciasDto;

public class Movimientos {
    private double monto;
    private long cuentaOrigen;
    private long cuentaDestino;
    private String tipoMoneda;
    private String tipoMovimiento;

    public Movimientos(MovimientosDto movimientosDto) {
        this.monto = movimientosDto.getMonto();
        this.cuentaOrigen = movimientosDto.getNumeroCuenta();
        this.tipoMoneda = movimientosDto.getTipoMoneda();
        this.tipoMovimiento = "DEPOSITO/RETIRO";
    }

    public Movimientos(MovimientosTransferenciasDto transferenciaDto) {
        this.monto = transferenciaDto.getMonto();
        this.cuentaOrigen = transferenciaDto.getNumeroCuentaOrigen();
        this.cuentaDestino = transferenciaDto.getNumeroCuentaDestino();
        this.tipoMoneda = transferenciaDto.getTipoMoneda();
        this.tipoMovimiento = "TRANSFERENCIA";
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

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
}
