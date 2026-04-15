package olmerk.rococo.service;

import olmerk.rococo.model.PaintingJson;
import olmerk.rococo.service.impl.PaintingDbClient;

import javax.annotation.Nonnull;

public interface PaintingClient {
    @Nonnull
    static PaintingClient getInstance() {
        return  new PaintingDbClient();
    }
    @Nonnull
    PaintingJson createPainting(PaintingJson paintingJson);

    void deletePainting(PaintingJson paintingJson);
}
