package ar.edu.utn.frbb.tup.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ar.edu.utn.frbb.tup.model.Cliente;
import ar.edu.utn.frbb.tup.exception.DatosIncorrectosException;
import ar.edu.utn.frbb.tup.persistence.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteDao extends AbstractBaseDao {

    public Cliente find(long dni) {
        try {
            Object entity = getInMemoryDatabase().get(dni);

            if (entity == null) {
                return null;
            }

            Cliente cliente = ((ClienteEntity) entity).toCliente();
            return cliente;

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el cliente con DNI: " + dni, e);
        }
    }

    public void save(Cliente cliente) throws DatosIncorrectosException {
        if (cliente == null) {
            throw new DatosIncorrectosException("El cliente no puede ser nulo");
        }

        try {
            ClienteEntity entity = new ClienteEntity(cliente);
            entity.setId(cliente.getDni());
            getInMemoryDatabase().put(entity.getId(), entity);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> getAll() {
        try {
            Map<Long, Object> database = getInMemoryDatabase();
            List<Cliente> clientes = new ArrayList<>();
            for (Object entity : database.values()) {
                clientes.add(((ClienteEntity) entity).toCliente());
            }
            return clientes;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los clientes: " + e.getMessage(), e);
        }
    }

    @Override
    protected String getEntityName() {
        return "CLIENTE";
    }

    public List<Cliente> findAllClientes() {
        try {
            List<Cliente> clientes = new ArrayList<>();
            for (Object object : getInMemoryDatabase().values()) {
                clientes.add(((ClienteEntity) object).toCliente());
            }
            return clientes;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar todos los clientes: " + e.getMessage(), e);
        }
    }
}