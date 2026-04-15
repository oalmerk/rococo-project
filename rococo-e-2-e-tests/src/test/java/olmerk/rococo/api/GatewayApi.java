package olmerk.rococo.api;

import retrofit2.Call;
import retrofit2.http.*;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.model.PaintingJson;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.model.page.RestPage;

import javax.annotation.Nullable;

public interface GatewayApi {
    @PATCH("api/user")
    Call<UserJson> updateUser(@Header("Authorization") String bearerToken, @Body UserJson userJson);

    @GET("api/user")
    Call<UserJson> getUser(@Header("Authorization") String bearerToken);

    @GET("api/artist")
    Call<RestPage<ArtistJson>> searchArtist(@Query("name") @Nullable String searchQuery);

    @GET("api/artist")
    Call<RestPage<ArtistJson>> paginationArtist(@Query("size") int size, @Query("page") int page);

    @POST("api/artist")
    Call<Void> createArtist(@Header("Authorization") String bearerToken, @Body ArtistJson artistJson);

    @GET("api/museum")
    Call<RestPage<MuseumJson>> searchMuseum(@Query("title") @Nullable String searchQuery);

    @GET("api/museum")
    Call<RestPage<MuseumJson>> paginationMuseum(@Query("size") int size, @Query("page") int page);

    @POST("api/museum")
    Call<Void> createMuseum(@Header("Authorization") String bearerToken, @Body MuseumJson museumJson);

    @GET("api/painting")
    Call<RestPage<PaintingJson>> searchPainting(@Query("title") @Nullable String searchQuery);

    @GET("api/painting")
    Call<RestPage<PaintingJson>> paginationPainting(@Query("size") int size, @Query("page") int page);

    @POST("api/painting")
    Call<Void> createPainting(@Header("Authorization") String bearerToken, @Body PaintingJson  paintingJson);
}
