package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.Museum;
import olmerk.rococo.model.*;
import olmerk.rococo.service.MuseumClient;
import olmerk.rococo.service.impl.MuseumDbClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;


public class MuseumExtension implements BeforeEachCallback, ParameterResolver , AfterEachCallback  {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
    private final MuseumClient museumClient = new MuseumDbClient();

    @Override
    public void beforeEach(ExtensionContext context){
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Museum.class)
                .ifPresent(museum -> {
                    CountryJson country = museumClient.getCountry(museum.countryName());
                    MuseumJson created = museumClient.createMuseum(new MuseumJson(null, "".equals(museum.title()) ? RandomDataUtils.artistName() : museum.title(),
                            museum.description(), PhotoUtils.fromFile(museum.photo()), new GeoJson(country, museum.city())));
                    setMuseum(created);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdMuseum();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext){
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Museum.class)
                .ifPresent(museum -> {
                    if (museum.deleteAfterTest()){
                        museumClient.deleteMuseum(createdMuseum());
                    }
                });
    }

    public static void setMuseum(MuseumJson museum) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museum
        );
    }

    public static MuseumJson createdMuseum() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), MuseumJson.class);
    }
}
