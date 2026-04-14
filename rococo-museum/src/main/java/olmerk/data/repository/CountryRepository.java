package olmerk.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import olmerk.data.CountryEntity;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
}
