package olmerk.rococo.data.repository;

import olmerk.rococo.data.entity.museum.MuseumEntity;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository {
    @Nonnull
    MuseumEntity create(MuseumEntity museumEntity);

    @Nonnull
    Optional<MuseumEntity> findById(UUID id);

    @Nonnull
    void deleteById(UUID id);
}
