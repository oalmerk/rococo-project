package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.Artists;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.service.ArtistClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;

public class ArtistCountExtension implements BeforeEachCallback, ParameterResolver , AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistCountExtension.class);
    private final ArtistClient artistClient = ArtistClient.getInstance();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Artists.class)
                .ifPresent(artists -> {
                    List<ArtistJson> artistJsonList = new ArrayList<>();
                    for (int i = 0; i < artists.countOfArtists(); i++) {
                        ArtistJson artist = artistClient.createArtist(new ArtistJson(null, RandomDataUtils.randomSentence(4),
                                RandomDataUtils.randomSentence(6), PhotoUtils.fromFile("images/pepin.jpg")));
                        artistJsonList.add(artist);
                    }
                    setArtists(artistJsonList);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson[].class);
    }

    @Override
    public ArtistJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdArtist().toArray(ArtistJson[]::new);
    }

    public static void setArtists(List<ArtistJson> artistJsonList) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                artistJsonList);
    }

    public static List<ArtistJson> createdArtist() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), List.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext){
        if(Objects.nonNull(createdArtist()) && !createdArtist().isEmpty()) {
            AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Artists.class)
                    .ifPresent(artists -> {
                        for (int i = 0; i < artists.countOfArtists(); i++) {
                            artistClient.deleteArtist(createdArtist().get(i));
                        }
                    });
        }
    }
}
