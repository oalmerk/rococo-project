package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.Artists;
import olmerk.rococo.jupiter.annotation.Museums;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.model.GeoJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.service.MuseumClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;

public class MuseumCountExtension implements BeforeEachCallback, ParameterResolver , AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumCountExtension.class);
    private final MuseumClient museumClient = MuseumClient.getInstance();

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Museums.class)
                .ifPresent(museums -> {
                    List<MuseumJson> museumJsonList = new ArrayList<>();
                    for (int i = 0; i < museums.countOfMuseums(); i++) {
                        CountryJson country = museumClient.getCountry("Россия");
                        MuseumJson created = museumClient.createMuseum(new MuseumJson(null, RandomDataUtils.randomName(),
                                RandomDataUtils.randomName(), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, RandomDataUtils.randomCity())));
                        museumJsonList.add(created);
                    }
                    setMuseums(museumJsonList);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson[].class);
    }

    @Override
    public MuseumJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdMuseums().toArray(MuseumJson[]::new);
    }

    public static void setMuseums(List<MuseumJson> museumJsonList) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                museumJsonList);
    }

    public static List<MuseumJson> createdMuseums() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), List.class);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext){
        if(Objects.nonNull(createdMuseums()) && !createdMuseums().isEmpty()){
            AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), Artists.class)
                    .ifPresent(artists -> {
                        for (int i = 0; i < artists.countOfArtists(); i++) {
                            museumClient.deleteMuseum(createdMuseums().get(i));
                        }
                    });
        }
    }
}
