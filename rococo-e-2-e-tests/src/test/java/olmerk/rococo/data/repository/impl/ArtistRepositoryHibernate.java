package olmerk.rococo.data.repository.impl;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.artist.ArtistEntity;
import olmerk.rococo.data.jpa.EntityManagers;
import olmerk.rococo.data.repository.ArtistRepository;

import java.util.Optional;
import java.util.UUID;

public class ArtistRepositoryHibernate implements ArtistRepository {

    private static final Config CONFIG = Config.getInstance();
    private static final EntityManager entityManager = EntityManagers.entityManager(CONFIG.artistJdbcUrl());

    @Override
    public @NonNull ArtistEntity create(ArtistEntity artist) {
        entityManager.joinTransaction();
        entityManager.persist(artist);
        return artist;
    }


    @Override
    public void deleteById(UUID id) {
        entityManager.joinTransaction();
        ArtistEntity artist = entityManager.find(ArtistEntity.class, id);
        if (artist != null) {
            entityManager.remove(artist);
        }
    }

    @Override
    public @NonNull Optional<ArtistEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(ArtistEntity.class, id)
        );
    }
}
