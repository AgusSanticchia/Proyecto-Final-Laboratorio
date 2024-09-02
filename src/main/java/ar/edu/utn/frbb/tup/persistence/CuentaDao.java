package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Cuenta;
import ar.edu.utn.frbb.tup.model.exception.CuentaNotExistException;
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

    public Cuenta find(long id) throws CuentaNotExistException {
        if (getInMemoryDatabase().get(id) == null) {
            return null;
        }
        return ((CuentaEntity) getInMemoryDatabase().get(id)).toCuenta();
    }

    public Cuenta update(Cuenta cuenta) throws CuentaNotExistException {
        CuentaEntity entity = (CuentaEntity) getInMemoryDatabase().get(cuenta.getNumeroCuenta());
        if (entity == null) {
            throw new CuentaNotExistException("La cuenta con número " + cuenta.getNumeroCuenta() + " no existe.");
        }
        entity.setBalance(cuenta.getBalance());
        entity.setTipoCuenta(cuenta.getTipoCuenta());
        entity.setMoneda(cuenta.getTipoMoneda());
        getInMemoryDatabase().put(cuenta.getNumeroCuenta(), entity);

        return entity.toCuenta();
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
}