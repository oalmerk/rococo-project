package olmerk.data.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import olmerk.data.MuseumEntity;

import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {
    @Nonnull
    Optional<MuseumEntity> findByTitle(@Nonnull String title);

    @Nonnull
    Page<MuseumEntity> findAllByTitleContainingIgnoreCase(
            @Nonnull String title,
            @Nonnull Pageable pageable
    );
}
