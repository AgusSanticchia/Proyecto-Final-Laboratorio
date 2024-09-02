package ar.edu.utn.frbb.tup.model;

import ar.edu.utn.frbb.tup.controller.dto.ClienteDto;
import ar.edu.utn.frbb.tup.model.enums.TipoCuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoMoneda;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Cliente extends Persona {

    private TipoPersona tipoPersona;
    private String banco;
    private LocalDate fechaAlta;
    private Set<Cuenta> cuentas = new HashSet<>();
    private String cbu;

    Random r = new Random();

    public Cliente(ClienteDto cliente) {
        super(cliente.getDni(), cliente.getApellido(), cliente.getNombre(), cliente.getFechaNacimiento(), cliente.getTelefono(), cliente.getDireccion());
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

    public void addCuenta(Cuenta cuenta) {
        if (cuenta != null) {
            this.cuentas.add(cuenta);
            cuenta.setTitular(this);
        }
    }

    public boolean tieneCuenta(TipoCuenta tipoCuenta, TipoMoneda moneda) {
        return cuentas.stream()
                .anyMatch(cuenta -> tipoCuenta.equals(cuenta.getTipoCuenta()) && moneda.equals(cuenta.getMoneda()));
    }

    public void setCuenta(List<Cuenta> cuentas) {
        this.cuentas = new HashSet<>(cuentas);
    }

    public String getCbu() {
        return cbu;
    }

    public void setCbu(String cbu) {
        this.cbu = String.valueOf(r.nextInt(900000) + 100000);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", tipoPersona=" + tipoPersona +
                ", banco='" + banco + '\'' +
                ", fechaAlta=" + fechaAlta +
                ", cuentas=" + cuentas +
                ", cbu='" + cbu + '\'' +
                '}';
    }


}
