package olmerk.rococo.data.repository;

import olmerk.rococo.data.entity.painting.PaintingEntity;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public interface PaintingRepository {
    @Nonnull
    PaintingEntity create(PaintingEntity museumEntity);

    @Nonnull
    Optional<PaintingEntity> findById(UUID id);

    void deleteById(UUID id);
}
