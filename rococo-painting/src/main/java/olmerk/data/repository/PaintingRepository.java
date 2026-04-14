package olmerk.data.repository;

import jakarta.annotation.Nonnull;
import olmerk.data.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {
    Page<PaintingEntity> findAll(Pageable pageable);

    @Nonnull
    Optional<PaintingEntity> findByNameContainsIgnoreCase(String name);

    @Nonnull
    Page<PaintingEntity> findAllByNameContainsIgnoreCase(
            @Nonnull String name,
            @Nonnull Pageable pageable
    );

    @Nonnull
    Optional<PaintingEntity> findAllByName(@Nonnull String name);
}
