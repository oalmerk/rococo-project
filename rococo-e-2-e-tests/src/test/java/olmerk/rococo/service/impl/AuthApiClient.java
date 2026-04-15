package olmerk.rococo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import olmerk.rococo.api.AuthApi;
import olmerk.rococo.api.core.CodeInterceptor;
import olmerk.rococo.api.core.ThreadSafeCookieStore;
import olmerk.rococo.config.Config;
import olmerk.rococo.jupiter.extension.ApiLoginExtension;
import olmerk.rococo.service.RestClient;
import olmerk.rococo.utils.OAuthUtils;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, HttpLoggingInterceptor.Level.BODY, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }


    @SneakyThrows
    public  String login(String userName, String password){
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        final String clientId = "client";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        authApi.login(
                userName,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        Response<JsonNode> tokenResponse = authApi.token(
                ApiLoginExtension.getTCode(),
                redirectUri,
                clientId,
                codeVerifier,
                "authorization_code"
        ).execute();

        return tokenResponse.body().get("id_token").asText();
    }
}
