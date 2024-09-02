package ar.edu.utn.frbb.tup.persistence;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBaseDao {
    protected static Map<String, Map<Long, Object>> poorMansDatabase = new HashMap<>();
    protected abstract String getEntityName();

    protected Map<Long, Object> getInMemoryDatabase() {
        poorMansDatabase.computeIfAbsent(getEntityName(), k -> new HashMap<>());
        return poorMansDatabase.get(getEntityName());
    }
}
