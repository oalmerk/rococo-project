package olmerk.rococo.data.repository.impl;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.museum.MuseumEntity;
import olmerk.rococo.data.jpa.EntityManagers;
import olmerk.rococo.data.repository.MuseumRepository;

import java.util.Optional;
import java.util.UUID;

public class MuseumRepositoryHibernate implements MuseumRepository {

    private static final Config CONFIG = Config.getInstance();
    private static final EntityManager entityManager = EntityManagers.entityManager(CONFIG.museumJdbcUrl());

    @Override
    public @NonNull MuseumEntity create(MuseumEntity museumEntity) {
        entityManager.joinTransaction();
        entityManager.persist(museumEntity);
        return museumEntity;
    }

    @Override
    public @NonNull Optional<MuseumEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(MuseumEntity.class, id)
        );
    }

    @Override
    public void deleteById(UUID id) {
        entityManager.joinTransaction();
        MuseumEntity museumEntity = entityManager.find(MuseumEntity.class, id);
        if (museumEntity != null) {
            entityManager.remove(museumEntity);
        }
    }
}
