package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.UsersClient;
import olmerk.rococo.service.impl.UserDbClient;
import olmerk.rococo.utils.RandomDataUtils;

import static olmerk.rococo.jupiter.extension.TestMethodContextExtension.context;


public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = "12345";

    private final UsersClient usersClient = new UserDbClient();

    @Override
    public void beforeEach(ExtensionContext context){
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.randomUsername();
                        final UserJson created = usersClient.createUser(username, DEFAULT_PASSWORD);
                        setUser(created);
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

    public static void setUser(UserJson testUser) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
    }

    public static UserJson createdUser() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), UserJson.class);
    }
}
