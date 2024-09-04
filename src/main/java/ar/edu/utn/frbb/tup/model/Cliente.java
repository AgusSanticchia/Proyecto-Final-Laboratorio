package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cliente extends Persona {

    private TipoPersona tipoPersona;
    private String banco;
    private LocalDate fechaAlta;
    private Set<Cuenta> cuentas = new HashSet<>();

    public Cliente(ClienteDto cliente) {
        super(cliente.getDni(), cliente.getDireccion(), cliente.getNombre(), cliente.getFechaNacimiento(), cliente.getTelefono(), cliente.getApellido());
        this.fechaAlta = LocalDate.now();
        this.tipoPersona = TipoPersona.fromString(cliente.getTipoPersona());
        this.banco = cliente.getBanco();
    }

    public Cliente() {}

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(TipoPersona tipoPersona) {
        this.tipoPersona = TipoPersona.fromString(tipoPersona.toString());
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Set<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuenta(List<Cuenta> cuentas) {
        this.cuentas = new HashSet<>(cuentas);
    }


    public void addCuenta(Cuenta cuenta) {
        this.cuentas.add(cuenta);
    }

    public int edad() {
        return Period.between(getFechaNacimiento(), LocalDate.now()).getYears();
    }

    public boolean tieneCuenta(TipoCuenta tipoCuenta, TipoMoneda moneda) {
        return cuentas.stream()
            .anyMatch(cuenta -> cuenta.getTipoCuenta().equals(tipoCuenta) && cuenta.getTipoMoneda().equals(moneda));
    }

    @Override
    public String toString() {
        
        return "Cliente{" +
                "nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", dni=" + getDni() +
                ", fechaNacimiento=" + getFechaNacimiento() +
                ", telefono='" + getTelefono() + '\'' +
                ", direccion='" + getDireccion() + '\'' +
                ", edad=" + edad() +
                ", tipoPersona=" + getTipoPersona() +
                ", banco='" + getBanco() + '\'' +
                ", fechaAlta=" + getFechaAlta() +
                ", cuentas=" + getCuentas() +
                '}';
    }


}
