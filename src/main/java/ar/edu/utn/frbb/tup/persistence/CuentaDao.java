package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.exception.cuentas.CuentaNotExistException;
import ar.edu.utn.frbb.tup.persistence.entity.CuentaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CuentaDao extends AbstractBaseDao {
    @Override
    protected String getEntityName() {
        return "CUENTA";
    }

    public void save(Cuenta cuenta) {
        CuentaEntity entity = new CuentaEntity(cuenta);
        getInMemoryDatabase().put(entity.getId(), entity);
    }

    public Cuenta find(long id){
        if (getInMemoryDatabase().get(id) == null) {
            return null;
        }
        return ((CuentaEntity) getInMemoryDatabase().get(id)).toCuenta();
    }

    public void update(Cuenta cuenta) throws CuentaNotExistException {
        CuentaEntity entity = (CuentaEntity) getInMemoryDatabase().get(cuenta.getNumeroCuenta());
        if (entity == null) {
            throw new CuentaNotExistException("Cuenta con id " + cuenta.getNumeroCuenta() + " no existe.");
        }
        entity.updateFromCuenta(cuenta);
        getInMemoryDatabase().put(entity.getId(), entity);
    }


    public List<Cuenta> getCuentasByCliente(long dni) {
        List<Cuenta> cuentasDelCliente = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            CuentaEntity cuentaEntity = ((CuentaEntity) object);
            if (cuentaEntity.getTitular() != null && cuentaEntity.getTitular().equals(dni)) {
                cuentasDelCliente.add(cuentaEntity.toCuenta());
            }
        }
        return cuentasDelCliente;
    }

    public List<Cuenta> findAll() {
        List<Cuenta> cuentas = new ArrayList<>();
        for (Object object : getInMemoryDatabase().values()) {
            CuentaEntity cuentaEntity = ((CuentaEntity) object);
            cuentas.add(cuentaEntity.toCuenta());
        }
        return cuentas;
    }
}