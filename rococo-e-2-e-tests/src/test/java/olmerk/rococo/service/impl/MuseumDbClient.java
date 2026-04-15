package olmerk.rococo.service.impl;

import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.museum.CountryEntity;
import olmerk.rococo.data.entity.museum.MuseumEntity;
import olmerk.rococo.data.repository.CountryRepository;
import olmerk.rococo.data.repository.MuseumRepository;
import olmerk.rococo.data.repository.impl.CountryRepositoryHibernate;
import olmerk.rococo.data.repository.impl.MuseumRepositoryHibernate;
import olmerk.rococo.data.templates.XaTransactionTemplate;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.service.MuseumClient;

import static java.util.Objects.requireNonNull;

public class MuseumDbClient implements MuseumClient {

    private static final Config CONFIG = Config.getInstance();
    private final MuseumRepository museumRepository = new MuseumRepositoryHibernate();
    private final CountryRepository countryRepository = new CountryRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CONFIG.museumJdbcUrl()
    );

    @Override
    public @NonNull MuseumJson createMuseum(MuseumJson museumJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            MuseumEntity museumEntity = museumRepository.create(entityFromJson(museumJson));
            return  MuseumJson.fromGJson(museumEntity);
        }));
    }

    @Override
    public @NonNull CountryJson getCountry(String name) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            CountryEntity countryEntity = countryRepository
                    .findByName(name)
                    .orElseThrow();
            return  CountryJson.fromJson(countryEntity);
        }));
    }


    @Override
    public void deleteMuseum(MuseumJson museumJson) {
        xaTransactionTemplate.execute(() -> {
            museumRepository.deleteById(museumJson.id());
            return null;
        });
    }

    private MuseumEntity entityFromJson(MuseumJson museumJson) {
        MuseumEntity museumEntity = new MuseumEntity();
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setId(museumJson.geo().country().id());
        museumEntity.setTitle(museumJson.title());
        museumEntity.setPhoto(museumJson.photo().getBytes());
        museumEntity.setDescription(museumJson.description());
        museumEntity.setCity(museumJson.geo().city());
        museumEntity.setCountry(countryEntity);
        return museumEntity;
    }
}
