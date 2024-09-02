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



    public void save(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        ClienteEntity entity = new ClienteEntity(cliente);
        entity.setId(cliente.getDni());
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
}
