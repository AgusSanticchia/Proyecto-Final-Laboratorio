package ar.edu.utn.frbb.tup.persistence.entity;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.enums.TipoPersona;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClienteEntity extends BaseEntity {

    private final String tipoPersona;
    private final String nombre;
    private final String apellido;
    private final LocalDate fechaAlta;
    private final LocalDate fechaNacimiento;
    private List<Cuenta> cuentas;

    public ClienteEntity(Cliente cliente) {
        super(cliente.getDni());
        this.tipoPersona = cliente.getTipoPersona() != null ? cliente.getTipoPersona().getDescripcion() : null;
        this.nombre = cliente.getNombre();
        this.apellido = cliente.getApellido();
        this.fechaAlta = cliente.getFechaAlta();
        this.fechaNacimiento = cliente.getFechaNacimiento();
        this.cuentas = new ArrayList<>();
        if (cliente.getCuentas() != null && !cliente.getCuentas().isEmpty()) {
            cuentas.addAll(cliente.getCuentas());
        }
    }

    public Cliente toCliente() {
        Cliente cliente = new Cliente();
        cliente.setDni(getId());
        cliente.setTipoPersona(TipoPersona.fromString(tipoPersona));
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setFechaAlta(fechaAlta);
        cliente.setFechaNacimiento(fechaNacimiento);

        return cliente;
    }
}
