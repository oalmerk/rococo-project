package olmerk.rococo.data.repository;

import olmerk.rococo.data.entity.museum.CountryEntity;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface CountryRepository {

    @Nonnull
    Optional<CountryEntity> findByName(String name);
}
