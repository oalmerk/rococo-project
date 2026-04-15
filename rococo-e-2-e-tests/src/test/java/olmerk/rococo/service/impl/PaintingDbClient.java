package olmerk.rococo.service.impl;

import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.painting.PaintingEntity;
import olmerk.rococo.data.repository.PaintingRepository;
import olmerk.rococo.data.repository.impl.PaintingRepositoryHibernate;
import olmerk.rococo.data.templates.XaTransactionTemplate;
import olmerk.rococo.model.PaintingJson;
import olmerk.rococo.service.PaintingClient;

import static java.util.Objects.requireNonNull;

public class PaintingDbClient implements PaintingClient {

    private static final Config CONFIG = Config.getInstance();
    private final PaintingRepository paintingRepository = new PaintingRepositoryHibernate();
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CONFIG.paintingJdbcUrl()
    );
    @Override
    public @NonNull PaintingJson createPainting(PaintingJson paintingJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            PaintingEntity paintingEntity = paintingRepository.create(entityFromJson(paintingJson));
            return  PaintingJson.fromJson(paintingEntity);
        }));
    }

    @Override
    public void deletePainting(PaintingJson paintingJson) {
        xaTransactionTemplate.execute(() -> {
            paintingRepository.deleteById(paintingJson.id());
            return null;
        });
    }

    private PaintingEntity entityFromJson(PaintingJson paintingJson) {
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setTitle(paintingJson.title());
        paintingEntity.setContent(paintingJson.content().getBytes());
        paintingEntity.setDescription(paintingJson.description());
        paintingEntity.setArtistId(paintingJson.artistId());
        paintingEntity.setMuseumId(paintingJson.museumId());
        return paintingEntity;
    }
}
