package olmerk.data.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import olmerk.data.ArtistEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {
    Page<ArtistEntity> findAll(Pageable pageable);

    @Nonnull
    Optional<ArtistEntity> findByNameContainsIgnoreCase(String name);

    @Nonnull
    Page<ArtistEntity> findAllByNameContainsIgnoreCase(
            @Nonnull String name,
            @Nonnull Pageable pageable
    );

    @Nonnull
    Optional<ArtistEntity> findAllByName(@Nonnull String name);
}
