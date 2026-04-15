package olmerk.rococo.test.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;
import retrofit2.Response;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Artists;
import olmerk.rococo.jupiter.annotation.Token;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.RestTest;
import olmerk.rococo.jupiter.extension.ApiLoginExtension;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.model.ErrorsJson;
import olmerk.rococo.service.impl.GateApiClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import java.io.IOException;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class ArtistRestTest {
    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();
    private final GateApiClient gateApiClient = new GateApiClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Artists(countOfArtists = 3)
    void searchArtistTest(ArtistJson[] artistJsonList){
        Page<ArtistJson> artistJsonListSearched = gateApiClient.searchArtists(artistJsonList[0].name());
        step("Check searched artist", () ->
                Assertions.assertAll(
                        () -> assertEquals(artistJsonListSearched.getTotalElements(), 1),
                        () -> assertEquals(artistJsonListSearched.getContent().get(0).name(), artistJsonList[0].name()),
                        () -> assertEquals(artistJsonListSearched.getContent().get(0).biography(), artistJsonList[0].biography())
                )
        );
    }


    @Test
    @Artists(countOfArtists = 3)
    void paginationArtistTest(){
        Page<ArtistJson> artistJsonListSearched1 = gateApiClient.paginationArtists(2, 0);
        step("Check searched artists", () ->
                Assertions.assertAll(
                        () -> assertEquals(2, artistJsonListSearched1.getTotalElements())
                )
        );
    }

    @Test
    void cantCreateArtistNotRegisteredUserTest(){
        gateApiClient.createArtistsWithError("Bearer " + RandomDataUtils.artistName(), new ArtistJson(null, RandomDataUtils.artistName(),
                        RandomDataUtils.artistName(), RandomDataUtils.artistName()),
                HttpStatus.UNAUTHORIZED_401);
    }

    @Test
    @User
    @ApiLogin
    void cantCreateArtistWithOutNameTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createArtistsWithError("Bearer " + bearerToken, new ArtistJson(null, "", RandomDataUtils.artistName(), ""),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(errors.errors().stream().sorted().toList(), List.of("Name can not be blank", "Allowed name length should be from 3 to 225 characters").stream().sorted().toList())
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateArtistWithShortNameTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createArtistsWithError("Bearer " + bearerToken, new ArtistJson(null, "QW", RandomDataUtils.artistName(), ""),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(errors.errors(), List.of("Allowed name length should be from 3 to 225 characters"))
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateArtistWithOutDescriptionTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createArtistsWithError("Bearer " + bearerToken, new ArtistJson(null, RandomDataUtils.artistName(), "" ,""),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Allowed biography length should be from 3 to 2000 characters", "Biography can not be blank").stream().sorted().toList(),
                        errors.errors().stream().sorted().toList()));
    }

    @Test
    @User
    @ApiLogin
    void cantCreateArtistWithShortDescriptionTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createArtistsWithError("Bearer " + bearerToken, new ArtistJson(null, RandomDataUtils.artistName(),"QW",  ""),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Allowed biography length should be from 3 to 2000 characters"), errors.errors())
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateArtistWithBigPhotoTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createArtistsWithError("Bearer " + bearerToken, new ArtistJson(null, RandomDataUtils.artistName(), RandomDataUtils.artistName(),
                        PhotoUtils.fromFile("images/bigphoto.jpg")),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Photo is should be 1Mb"), errors.errors())
        );
    }
}
