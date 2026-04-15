package olmerk.rococo.data.repository.impl;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.painting.PaintingEntity;
import olmerk.rococo.data.jpa.EntityManagers;
import olmerk.rococo.data.repository.PaintingRepository;

import java.util.Optional;
import java.util.UUID;

public class PaintingRepositoryHibernate implements PaintingRepository {

    private static final Config CONFIG = Config.getInstance();
    private static final EntityManager entityManager = EntityManagers.entityManager(CONFIG.paintingJdbcUrl());
    @Override
    public @NonNull PaintingEntity create(PaintingEntity museumEntity) {
        entityManager.joinTransaction();
        entityManager.persist(museumEntity);
        return museumEntity;
    }

    @Override
    public @NonNull Optional<PaintingEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(PaintingEntity.class, id)
        );
    }

    @Override
    public void deleteById(UUID id) {
        entityManager.joinTransaction();
        PaintingEntity paintingEntity = entityManager.find(PaintingEntity.class, id);
        if (paintingEntity != null) {
            entityManager.remove(paintingEntity);
        }
    }
}
