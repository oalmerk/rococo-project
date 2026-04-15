package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.Artists;
import olmerk.rococo.jupiter.annotation.Paintings;
import olmerk.rococo.model.*;
import olmerk.rococo.service.ArtistClient;
import olmerk.rococo.service.MuseumClient;
import olmerk.rococo.service.PaintingClient;
import olmerk.rococo.service.impl.MuseumDbClient;
import olmerk.rococo.service.impl.PaintingDbClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;

public class PaintingsCountExtension implements BeforeEachCallback, ParameterResolver , AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PaintingsCountExtension.class);
    private final PaintingClient paintingClient = new PaintingDbClient();
    private final ArtistClient artistClient = ArtistClient.getInstance();
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Paintings.class)
                .ifPresent(paintings -> {
                    List<PaintingJson> paintingJsonList = new ArrayList<>();
                    for (int i = 0; i < paintings.countOfPaintings(); i++) {
                        ArtistJson artistJson = artistClient.createArtist(new ArtistJson(null, RandomDataUtils.randomSentence(4),
                                RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/kandinskiy2.jpg")));
                        CountryJson country = museumClient.getCountry("Россия");
                        MuseumJson museumJson = museumClient.createMuseum(new MuseumJson(null, RandomDataUtils.randomSentence(4),
                                RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, "Санкт-Петербург")));
                        PaintingJson paintingJson = paintingClient.createPainting(new PaintingJson(null,  RandomDataUtils.randomSentence(4),
                                RandomDataUtils.randomSentence(5), PhotoUtils.fromFile("images/сomposition8.jpg"), artistJson.id(), museumJson.id()));
                        paintingJsonList.add(paintingJson);
                    }
                    setPaintings(paintingJsonList);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(PaintingJson[].class);
    }

    @Override
    public PaintingJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdPaintings().toArray(PaintingJson[]::new);
    }

    public static void setPaintings(List<PaintingJson> paintingJsonList) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                paintingJsonList);
    }

    public static List<PaintingJson> createdPaintings() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), List.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext){
        if(Objects.nonNull(createdPaintings()) && !createdPaintings().isEmpty()){
            AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Artists.class)
                    .ifPresent(artists -> {
                        for (int i = 0; i < artists.countOfArtists(); i++) {
                            paintingClient.deletePainting(createdPaintings().get(i));
                        }
                    });
        }
    }
}
