package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.CuentaDto;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class Cuenta {
    Random r = new Random();
    private TipoMoneda tipoMoneda;
    private long dniTitular;
    private TipoCuenta tipoCuenta;
    private long numeroCuenta;
    private LocalDateTime fechaCreacion;
    private String cbu;
    private Double balance = 0d;
    LinkedList<Movimientos> movimientos;

    public Cuenta(){
        this.balance = getBalance();
        this.fechaCreacion = LocalDateTime.now();
    }

    public Cuenta(CuentaDto cuentaDto) {
        this.tipoMoneda = TipoMoneda.fromString(cuentaDto.getTipoMoneda());
        this.balance = getBalance();
        this.dniTitular = cuentaDto.getDniTitular();
        this.fechaCreacion = LocalDateTime.now();
        this.movimientos = new LinkedList<>();
        this.numeroCuenta = numeroCuenta();
        this.tipoCuenta = TipoCuenta.fromString(cuentaDto.getTipoCuenta());
        this.cbu = cuentaDto.getCbu() != null ? cuentaDto.getCbu() : generarCbu();
    }

    public Cuenta(long l, TipoMoneda tipoMoneda, double v) {
        this.dniTitular = l;
        this.balance = v;
        this.cbu = generarCbu();
        this.movimientos = new LinkedList<>();
        this.tipoCuenta = TipoCuenta.CAJA_AHORRO_PESOS;
        this.tipoMoneda = tipoMoneda;
        this.fechaCreacion = LocalDateTime.now();
        this.numeroCuenta = numeroCuenta();
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

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public String generarCbu(){
        StringBuilder cbu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cbu.append(r.nextInt(10));
        }
        return this.cbu = cbu.toString();
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

    public void addMovimiento(Movimientos movimiento) {
        this.movimientos.add(movimiento);
    }

    public long numeroCuenta() {
        Random random = new Random();
        return random.nextLong(100 + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return tipoMoneda == cuenta.tipoMoneda && tipoCuenta == cuenta.tipoCuenta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoMoneda, tipoCuenta);
    }
}
