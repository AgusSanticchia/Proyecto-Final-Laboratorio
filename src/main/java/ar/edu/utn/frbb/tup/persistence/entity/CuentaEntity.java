package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDateTime;

public class CuentaEntity extends BaseEntity{
    String nombre;
    long contadorCuenta;
    LocalDateTime fechaCreacion;
    double balance;
    String tipoCuenta;
    Long titular;
    long numeroCuenta;
    TipoMoneda tipoMoneda;
    String cbu;

    public CuentaEntity(Cuenta cuenta) {
        super(cuenta.getNumeroCuenta());
        this.balance = cuenta.getBalance();
        this.tipoCuenta = cuenta.getTipoCuenta().toString();
        this.titular = cuenta.getDniTitular();
        this.fechaCreacion = cuenta.getFechaCreacion();
        this.numeroCuenta = cuenta.getNumeroCuenta();
        this.cbu = cuenta.getCbu();
        this.tipoMoneda = cuenta.getTipoMoneda();
        
    }

    public void updateFromCuenta(Cuenta cuenta) {
        this.balance = cuenta.getBalance();
        this.tipoMoneda = cuenta.getTipoMoneda();
        this.titular = cuenta.getDniTitular();
    }

    public Cuenta toCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setBalance(this.balance);
        cuenta.setNumeroCuenta(this.numeroCuenta);
        cuenta.setTipoCuenta(TipoCuenta.valueOf(this.tipoCuenta));
        cuenta.setDniTitular(this.titular);
        cuenta.setFechaCreacion(this.fechaCreacion);
        cuenta.setTipoMoneda(this.tipoMoneda);
        cuenta.setCbu(this.cbu);

        return cuenta;
    }

    public Long getTitular() {
        return titular;
    }

}
