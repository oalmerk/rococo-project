package olmerk.rococo.service;

import org.jspecify.annotations.NonNull;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.impl.UserApiClient;
import olmerk.rococo.service.impl.UserDbClient;

import javax.annotation.Nonnull;

public interface UsersClient {

    @Nonnull
    static UsersClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new UserApiClient()
                : new UserDbClient();
    }

    @Nonnull
    UserJson createUser(String username, String password);

    @Nonnull
    UserJson updateUser(UserJson userJson);

    @NonNull UserJson getUser(UserJson userJson);
}
