package olmerk.rococo.service;

import olmerk.rococo.model.CountryJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.service.impl.MuseumDbClient;

import javax.annotation.Nonnull;

public interface MuseumClient {

    @Nonnull
    static MuseumClient getInstance() {
        return  new MuseumDbClient();
    }
    @Nonnull
    MuseumJson createMuseum(MuseumJson artistJson);

    @Nonnull
    CountryJson getCountry(String name);

    void deleteMuseum(MuseumJson artistJson);
}
