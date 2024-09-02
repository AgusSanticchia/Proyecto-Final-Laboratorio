package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Random;

public class Cuenta {
    private TipoMoneda tipoMoneda;
    private long dniTitular;
    private TipoCuenta tipoCuenta;
    private long numeroCuenta;
    private LocalDateTime fechaCreacion;
    private Double balance = 0d;
    LinkedList<Movimientos> movimientos;

    public Cuenta(){}
    public Cuenta(CuentaDto cuentaDto) {
        this.tipoMoneda = TipoMoneda.fromString(cuentaDto.getTipoMoneda());
        this.balance = getBalance();
        this.dniTitular = cuentaDto.getDniTitular();
        this.fechaCreacion = LocalDateTime.now();
        this.movimientos = new LinkedList<>();
        this.numeroCuenta = numeroCuenta();
        this.tipoCuenta = TipoCuenta.fromString(cuentaDto.getTipoCuenta());
    }

    public TipoMoneda getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(TipoMoneda tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public long getDniTitular() {
        return dniTitular;
    }

    public void setDniTitular(long dniTitular) {
        this.dniTitular = dniTitular;
    }

    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public LinkedList<Movimientos> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(LinkedList<Movimientos> movimientos) {
        this.movimientos = movimientos;
    }

    public long numeroCuenta() {
        Random random = new Random();
        return random.nextLong();
    }

}
