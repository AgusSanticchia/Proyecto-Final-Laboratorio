package ar.edu.utn.frbb.tup.controller.dto;

public class CuentaDto {
    private long id;
    private long numeroCuenta;
    private double saldo;
    private String tipoCuenta;
    private String tipoMoneda;
    private String titular;

    public CuentaDto(long id, long numeroCuenta, double saldo, String tipoCuenta, String tipoMoneda, String titular) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.tipoCuenta = tipoCuenta;
        this.tipoMoneda = tipoMoneda;
        this.titular = titular;
    }

    public long getId() {return id;}

    public long getNumeroCuenta() {return numeroCuenta;}

    public double getSaldo() {return saldo;}

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public String getTitular() {return titular;}
}