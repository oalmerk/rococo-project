package olmerk.data.repository;

import jakarta.annotation.Nonnull;
import olmerk.data.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
    @Nonnull
    Optional<PaintingEntity> findByTitle(@Nonnull String title);

    @Nonnull
    Page<PaintingEntity> findAllByTitleContainingIgnoreCase(
            @Nonnull String title,
            @Nonnull Pageable pageable
    );

    @Nonnull
    Page<PaintingEntity> findAllByArtistId(
            @Nonnull UUID title,
            @Nonnull Pageable pageable
    );
}
