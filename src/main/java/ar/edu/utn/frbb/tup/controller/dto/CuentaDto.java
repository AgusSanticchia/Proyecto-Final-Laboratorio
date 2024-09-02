package ar.edu.utn.frbb.tup.controller.dto;

public class CuentaDto {
    private long dniTitular;
    private String tipoCuenta;
    private String tipoMoneda;

    public CuentaDto(long dniTitular, String tipoCuenta, String tipoMoneda) {
        this.dniTitular = dniTitular;
        this.tipoCuenta = tipoCuenta;
        this.tipoMoneda = tipoMoneda;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public long getDniTitular() {
        return dniTitular;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }
}