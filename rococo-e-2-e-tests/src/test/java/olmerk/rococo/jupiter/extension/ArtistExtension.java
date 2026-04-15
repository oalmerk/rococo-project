package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.Artist;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.service.ArtistClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;

public class ArtistExtension implements BeforeEachCallback, ParameterResolver , AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
    private final ArtistClient artistClient = ArtistClient.getInstance();

    @Override
    public void beforeEach(ExtensionContext extensionContext){
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Artist.class)
                .ifPresent(artist -> {
                    ArtistJson created = artistClient.createArtist(new ArtistJson(null, "".equals(artist.name()) ? RandomDataUtils.artistName() : artist.name(),
                            artist.biography(), PhotoUtils.fromFile(artist.photo())));
                    setArtist(created);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(ArtistJson.class);
    }

    @Override
    public ArtistJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdArtist();
    }

    public static void setArtist(ArtistJson created) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                created
        );
    }

    public static ArtistJson createdArtist() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), ArtistJson.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext){
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Artist.class)
                .ifPresent(artist -> {
                   artistClient.deleteArtist(createdArtist());
                });
    }
}
