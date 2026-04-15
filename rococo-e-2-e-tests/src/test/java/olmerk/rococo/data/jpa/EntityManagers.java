package olmerk.rococo.data.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.commons.lang3.StringUtils;
import olmerk.rococo.data.jdbc.DataSources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {

    private static final Map<String, EntityManagerFactory> ENTITY_MANAGER_FACTORY_MAP = new ConcurrentHashMap<>();

    private EntityManagers() {
    }

    public static EntityManager entityManager(String jdbcUrl){
        return new TreadSafeEntityManager(ENTITY_MANAGER_FACTORY_MAP.computeIfAbsent( jdbcUrl, key -> {
            DataSources.dataSource(jdbcUrl);
            final String persistenceUnitName = StringUtils.substringAfter(jdbcUrl, "5432/");
            return Persistence.createEntityManagerFactory(persistenceUnitName);
        }).createEntityManager());
    }
}
