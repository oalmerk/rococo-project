package olmerk.rococo.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import olmerk.rococo.model.UserJson;

public interface UserDataApi {

    @GET("api/user")
    Call<UserJson> currentUser(@Query("username") String username);
}
