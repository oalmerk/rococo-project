package olmerk.rococo.data.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.museum.CountryEntity;
import olmerk.rococo.data.jpa.EntityManagers;
import olmerk.rococo.data.repository.CountryRepository;

import java.util.Optional;

public class CountryRepositoryHibernate implements CountryRepository {

    private static final Config CONFIG = Config.getInstance();
    private static final EntityManager entityManager = EntityManagers.entityManager(CONFIG.museumJdbcUrl());

    @Override
    public @NonNull Optional<CountryEntity> findByName(String name) {
        try {
            return Optional.ofNullable(
                    entityManager.createQuery(
                                    "SELECT c FROM CountryEntity c WHERE c.name = :name",
                                    CountryEntity.class
                            )
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
