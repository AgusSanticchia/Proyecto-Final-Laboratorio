package ar.edu.utn.frbb.tup.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.persistence.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteDao extends AbstractBaseDao {

    public Cliente find(long dni) {
        try {
            // Busco el cliente en la base de datos
            Object entity = getInMemoryDatabase().get(dni);

            // Si no existe, devuelvo null
            if (entity == null) {
                return null;
            }

            // Convierto la entidad a un objeto de dominio
            Cliente cliente = ((ClienteEntity) entity).toCliente();

            // Devuelvo el cliente
            return cliente;

        } catch (Exception e) {
            // Si ocurre un error, lanzo una excepcion
            throw new RuntimeException("Error al buscar el cliente con DNI: " + dni, e);
        }
    }

    public void save(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        // Creo una entidad a partir del objeto de dominio
        ClienteEntity entity = new ClienteEntity(cliente);

        // Le asigno un id (que sera el DNI del cliente)
        entity.setId(cliente.getDni());

        // Persisto la entidad en la base de datos
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public List<Cliente> getAll() {
        Map<Long, Object> database = getInMemoryDatabase();
        List<Cliente> clientes = new ArrayList<>();
        for (Object entity : database.values()) {
            clientes.add(((ClienteEntity) entity).toCliente());
        }
        return clientes;
    }

    @Override
    protected String getEntityName() {
        return "CLIENTE";
    }

    public List<Cliente> findAllClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            clientes.add(((ClienteEntity) object).toCliente());
        }
        return clientes;
    }
}
