package olmerk.rococo.service;

import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.service.impl.ArtistApiClient;
import olmerk.rococo.service.impl.ArtistDbClient;

import javax.annotation.Nonnull;

public interface ArtistClient {
    @Nonnull
    static ArtistClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new ArtistApiClient()
                : new ArtistDbClient();
    }
    @Nonnull
    ArtistJson createArtist(ArtistJson artistJson);

    @Nonnull
    void deleteArtist(ArtistJson artistJson);
}
