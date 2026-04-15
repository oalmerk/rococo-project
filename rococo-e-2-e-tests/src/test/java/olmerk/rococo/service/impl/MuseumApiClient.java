package olmerk.rococo.service.impl;

import org.jspecify.annotations.NonNull;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.service.MuseumClient;

public class MuseumApiClient implements MuseumClient {
    @Override
    public @NonNull MuseumJson createMuseum(MuseumJson artistJson) {
        return null;
    }

    @Override
    public @NonNull CountryJson getCountry(String name) {
        return null;
    }


    @Override
    public void deleteMuseum(MuseumJson artistJson) {

    }
}
