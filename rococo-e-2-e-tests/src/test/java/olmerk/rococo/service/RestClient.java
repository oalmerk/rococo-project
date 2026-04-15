package olmerk.rococo.service;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import olmerk.rococo.api.core.ThreadSafeCookieStore;
import olmerk.rococo.config.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Arrays;

public abstract class RestClient {

    private static final Config CONFIG = Config.getInstance();
    private final OkHttpClient client;
    private final Retrofit retrofit;


    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, null);
    }

    public RestClient(String baseUrl, HttpLoggingInterceptor.Level level) {
        this(baseUrl, false, JacksonConverterFactory.create(), level);
    }


    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, null);
    }

    public RestClient(String baseUrl, boolean followRedirect, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), level, interceptors);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory) {
        this(baseUrl, followRedirect, factory, HttpLoggingInterceptor.Level.BODY, null);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, Interceptor... interceptor) {
        this(baseUrl, followRedirect, factory, HttpLoggingInterceptor.Level.BODY, interceptor);
    }

    public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level,@Nullable Interceptor... interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);
        if(interceptors != null){
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        builder
                .cookieJar(
                        new JavaNetCookieJar(
                           new CookieManager(
                                   ThreadSafeCookieStore.INSTANCE,
                                   CookiePolicy.ACCEPT_ALL
                           )
                        )
                )
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        this.client = builder.build();
        this.retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(factory)
                .build();
    }


    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }

    public static Response executeResponseBody(Call<Void> call, int... expectedStatus) {
        final Response<Void> response = doExecute(call);
        assertStatus(response, expectedStatus);
        return response;
    }

    public static <T> @Nonnull T executeForBody(Call<T> call, int... expectedStatus) {
        final Response<T> response = doExecute(call);
        assertStatus(response, expectedStatus);
        final T body = response.body();
        if (body == null) {
            final Request rq = response.raw().request();
            throw new AssertionError("Response body is null for "
                    + rq.method() + " " + rq.url()
                    + " (status=" + response.code() + ")");
        }
        return body;
    }

    private static <T> Response<T> doExecute(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private static void assertStatus(Response<?> response, int... expected) {
        final int code = response.code();
        for (int ok : expected) {
            if (ok == code) return;
        }
        String err = null;
        try {
            if (response.errorBody() != null) {
                err = response.errorBody().string();
            }
        } catch (IOException ignored) {}
        final Request rq = response.raw().request();
        throw new AssertionError(
                "Unexpected HTTP status " + code + " for " + rq.method() + " " + rq.url()
                        + ", expected " + Arrays.toString(expected)
                        + (err != null && !err.isBlank() ? (", errorBody=" + err) : "")
        );
    }

    @ParametersAreNonnullByDefault
    public static final class EmtyRestClient extends RestClient {
        public EmtyRestClient(String baseUrl) {
            super(baseUrl);
        }

        public EmtyRestClient(String baseUrl, HttpLoggingInterceptor.Level level) {
            super(baseUrl, level);
        }
    }
}
