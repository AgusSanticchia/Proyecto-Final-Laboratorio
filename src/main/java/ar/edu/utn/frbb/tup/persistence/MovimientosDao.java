package ar.edu.utn.frbb.tup.persistence;

import ar.edu.utn.frbb.tup.model.Movimientos;
import org.springframework.stereotype.Repository;

@Repository
public class MovimientosDao extends AbstractBaseDao {
    @Override
    protected String getEntityName() {
        return "MOVIMIENTOS";
    }

    public void transferir(Movimientos movimientos) {
        getInMemoryDatabase().put(movimientos.getMovimientoId(), movimientos);
    }

    public void depositar(Movimientos movimientos) {
        getInMemoryDatabase().put(movimientos.getMovimientoId(), movimientos);
    }

    public void retirar(Movimientos movimientos) {
        getInMemoryDatabase().put(movimientos.getMovimientoId(), movimientos);
    }
}