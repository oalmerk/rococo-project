package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.Painting;
import olmerk.rococo.model.*;
import olmerk.rococo.service.ArtistClient;
import olmerk.rococo.service.MuseumClient;
import olmerk.rococo.service.PaintingClient;
import olmerk.rococo.service.impl.MuseumDbClient;
import olmerk.rococo.service.impl.PaintingDbClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import static olmerk.rococo.jupiter.extension.ArtistExtension.createdArtist;
import static olmerk.rococo.jupiter.extension.ArtistExtension.setArtist;
import static olmerk.rococo.jupiter.extension.MuseumExtension.createdMuseum;
import static olmerk.rococo.jupiter.extension.MuseumExtension.setMuseum;
import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;


public class PaintingExtension implements BeforeEachCallback, ParameterResolver, AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingExtension.class);
    private final PaintingClient paintingClient = new PaintingDbClient();
    private final ArtistClient artistClient = ArtistClient.getInstance();
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Painting.class)
                .ifPresent(painting -> {
                    ArtistJson artistJson;
                    MuseumJson museumJson;
                    if (painting.createNewArtist()) {
                        artistJson = artistClient.createArtist(new ArtistJson(null, RandomDataUtils.artistName(),
                                RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/kandinskiy2.jpg")));
                        setArtist(artistJson);
                    } else {
                        artistJson = createdArtist();
                    }
                    if (painting.createNewMuseum()) {
                        CountryJson country = museumClient.getCountry("Россия");
                        museumJson = museumClient.createMuseum(new MuseumJson(null, "Русский музей",
                                RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, "Санкт-Петербург")));
                        setMuseum(museumJson);
                    } else {
                        museumJson = createdMuseum();
                    }
                    PaintingJson paintingJson = paintingClient.createPainting(new PaintingJson(null, "".equals(painting.title()) ? RandomDataUtils.artistName() : painting.title(),
                            "".equals(painting.description()) ? RandomDataUtils.randomSentence(6) : painting.description(), PhotoUtils.fromFile(painting.content()), artistJson.id(), museumJson.id()));
                    setPainting(paintingJson);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdPainting();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Painting.class)
                .ifPresent(painting -> {
                    if (painting.deleteAfterTest()) {
                        paintingClient.deletePainting(createdPainting());
                    }
                    if (painting.createNewArtist()) {
                        artistClient.deleteArtist(createdArtist());
                    }
                    if (painting.createNewMuseum()) {
                        museumClient.deleteMuseum(createdMuseum());
                    }
                });
    }

    public static void setPainting(PaintingJson paintingJson) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                paintingJson
        );
    }

    public static PaintingJson createdPainting() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), PaintingJson.class);
    }
}
