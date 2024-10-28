package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.Movimientos;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class CuentaEntity extends BaseEntity{
    String nombre;
    LocalDateTime fechaCreacion;
    double balance;
    String tipoCuenta;
    Long titular;
    long numeroCuenta;
    TipoMoneda tipoMoneda;
    String cbu;
    LinkedList<Movimientos> movimientos;

    public CuentaEntity(Cuenta cuenta) {
        super(cuenta.getNumeroCuenta());
        this.balance = cuenta.getBalance();
        this.tipoCuenta = cuenta.getTipoCuenta().toString();
        this.titular = cuenta.getDniTitular();
        this.fechaCreacion = cuenta.getFechaCreacion();
        this.numeroCuenta = cuenta.getNumeroCuenta();
        this.cbu = cuenta.getCbu();
        this.tipoMoneda = cuenta.getTipoMoneda();
        this.movimientos = cuenta.getMovimientos();
    }
    public void updateFromCuenta(Cuenta cuenta) {
        // Update the balance and tipoMoneda from the cuenta
        this.balance = cuenta.getBalance();
        this.tipoMoneda = cuenta.getTipoMoneda();
        this.titular = cuenta.getDniTitular();
        this.movimientos = cuenta.getMovimientos();
    }

    public Cuenta toCuenta() {
        // Create a new cuenta object
        Cuenta cuenta = new Cuenta();

        // Set the data from the entity to the cuenta object
        cuenta.setBalance(this.balance);
        cuenta.setNumeroCuenta(this.numeroCuenta);
        // Use the valueOf method to convert the String to a TipoCuenta enum
        cuenta.setTipoCuenta(TipoCuenta.valueOf(this.tipoCuenta));
        cuenta.setDniTitular(this.titular);
        cuenta.setFechaCreacion(this.fechaCreacion);
        cuenta.setTipoMoneda(this.tipoMoneda);
        cuenta.setCbu(this.cbu);
        cuenta.setMovimientos(this.movimientos);

        return cuenta;
    }

    public Long getTitular() {
        return titular;
    }

}
