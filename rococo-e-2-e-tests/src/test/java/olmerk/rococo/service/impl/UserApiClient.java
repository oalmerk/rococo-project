package olmerk.rococo.service.impl;

import org.jspecify.annotations.NonNull;
import olmerk.rococo.api.AuthApi;
import olmerk.rococo.api.GatewayApi;
import olmerk.rococo.api.UserDataApi;
import olmerk.rococo.api.core.ThreadSafeCookieStore;
import olmerk.rococo.config.Config;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.RestClient;
import olmerk.rococo.service.UsersClient;

import static java.util.Objects.requireNonNull;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;
import static olmerk.rococo.service.RestClient.executeForBody;
import static olmerk.rococo.service.RestClient.executeResponseBody;

public class UserApiClient implements UsersClient {

    private static final Config CONFIG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmtyRestClient(CONFIG.authUrl(), NONE).create(AuthApi.class);
    private final UserDataApi userdataApi = new RestClient.EmtyRestClient(CONFIG.userdataUrl(), NONE).create(UserDataApi.class);
    private final GatewayApi gatewayApi = new RestClient.EmtyRestClient(CONFIG.gatewayUrl(), NONE).create(GatewayApi.class);

    @Override
    public @NonNull UserJson createUser(String username, String password) {
        executeResponseBody(authApi.requestRegisterForm(), 200);
        executeResponseBody(
                authApi.register(
                        username,
                        password,
                        password,
                        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                ),
                200
        );
        UserJson createdUser = requireNonNull(executeForBody(
                userdataApi.currentUser(username),
                200
        ));
        return createdUser;
    }

    @Override
    public @NonNull UserJson updateUser(UserJson userJson) {
//        UserJson updatedUser = requireNonNull(executeForBody(
//                gatewayApi.updateUser(token, userJson),
//                200
//        ));
        return userJson;
    }

    @Override
    public @NonNull UserJson getUser(UserJson userJson) {
        return requireNonNull(executeForBody(
                userdataApi.currentUser(userJson.username()),
                200
        ));
    }
}
