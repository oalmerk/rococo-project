package olmerk.rococo.data.repository;

import olmerk.rococo.data.entity.artist.ArtistEntity;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository {
    @Nonnull
    ArtistEntity create(ArtistEntity artist);

    void deleteById(UUID id);

    @Nonnull
    Optional<ArtistEntity> findById(UUID id);
}
