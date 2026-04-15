package olmerk.rococo.service.impl;

import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.artist.ArtistEntity;
import olmerk.rococo.data.repository.ArtistRepository;
import olmerk.rococo.data.repository.impl.ArtistRepositoryHibernate;
import olmerk.rococo.data.templates.JdbcTransactionTemplate;
import olmerk.rococo.data.templates.XaTransactionTemplate;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.service.ArtistClient;

import static java.util.Objects.requireNonNull;

public class ArtistDbClient implements ArtistClient {

    private static final Config CONFIG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CONFIG.artistJdbcUrl()
    );

    private final JdbcTransactionTemplate jdbcTransactionTemplate = new JdbcTransactionTemplate(
            CONFIG.artistJdbcUrl()
    );

    @Override
    public @NonNull ArtistJson createArtist(ArtistJson artistJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            ArtistEntity artistEntity = artistRepository.create(entityFromJson(artistJson));
            return  ArtistJson.fromEntity(artistEntity);
        }));
    }


    @Override
    public void deleteArtist(ArtistJson artistJson) {
      xaTransactionTemplate.execute(() -> {
          artistRepository.deleteById(artistJson.id());
          return null;
      });
    }

    private ArtistEntity entityFromJson(ArtistJson artistJson) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setName(artistJson.name());
        artistEntity.setPhoto(artistJson.photo().getBytes());
        artistEntity.setBiography(artistJson.biography());
       return artistEntity;
    }
}

