package olmerk.rococo.service.impl;

import okhttp3.logging.HttpLoggingInterceptor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import retrofit2.Response;
import olmerk.rococo.api.GatewayApi;
import olmerk.rococo.api.core.CodeInterceptor;
import olmerk.rococo.config.Config;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.model.PaintingJson;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.RestClient;

import static java.util.Objects.requireNonNull;

public class GateApiClient extends RestClient {

    private static final Config CONFIG = Config.getInstance();
    private final GatewayApi gatewayApi;

    public GateApiClient() {
        super(CONFIG.gatewayUrl(), true, HttpLoggingInterceptor.Level.BODY, new CodeInterceptor());
        this.gatewayApi = create(GatewayApi.class);
    }

    public @NonNull UserJson updateUser(String token, UserJson userJson) {
        UserJson updatedUser = requireNonNull(executeForBody(
                gatewayApi.updateUser(token, userJson),
                200
        ));
        return updatedUser;
    }

    public @NonNull UserJson getUser(String token) {
        UserJson user = requireNonNull(executeForBody(
                gatewayApi.getUser(token),
                200
        ));
        return user;
    }

    public @NonNull Page<ArtistJson> searchArtists(String name) {
        Page<ArtistJson> artistJsonList = requireNonNull(executeForBody(
                gatewayApi.searchArtist(name),
                200
        ));
        return artistJsonList;
    }

    public @NonNull Page<MuseumJson> searchMuseum(String title) {
        Page<MuseumJson> museumJsons = requireNonNull(executeForBody(
                gatewayApi.searchMuseum(title),
                200
        ));
        return museumJsons;
    }

    public @NonNull Page<PaintingJson> searchPainting(String title) {
        Page<PaintingJson> paintingJsonPage = requireNonNull(executeForBody(
                gatewayApi.searchPainting(title),
                200
        ));
        return paintingJsonPage;
    }

    public @NonNull Page<ArtistJson> paginationArtists(int size, int page) {
        Page<ArtistJson> artistJsonList = requireNonNull(executeForBody(
                gatewayApi.paginationArtist(size, page),
                200
        ));
        return artistJsonList;
    }

    public @NonNull Page<MuseumJson> paginationMuseums(int size, int page) {
        Page<MuseumJson> museumJsons = requireNonNull(executeForBody(
                gatewayApi.paginationMuseum(size, page),
                200
        ));
        return museumJsons;
    }

    public @NonNull Page<PaintingJson> paginationPainting(int size, int page) {
        Page<PaintingJson> paintingJsonPage = requireNonNull(executeForBody(
                gatewayApi.paginationPainting(size, page),
                200
        ));
        return paintingJsonPage;
    }

    public @NonNull Response createArtistsWithError(String token, ArtistJson artistJson, int httpStatus) {
        return executeResponseBody(gatewayApi.createArtist(token, artistJson), httpStatus);
    }

    public @NonNull Response createMuseumWithError(String token, MuseumJson museumJson, int httpStatus) {
        return executeResponseBody(gatewayApi.createMuseum(token, museumJson), httpStatus);
    }

    public @NonNull Response createPaintingWithError(String token, PaintingJson  paintingJson, int httpStatus) {
        return executeResponseBody(gatewayApi.createPainting(token, paintingJson), httpStatus);
    }
}
