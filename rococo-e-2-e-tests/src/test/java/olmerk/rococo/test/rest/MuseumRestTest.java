package olmerk.rococo.test.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;
import retrofit2.Response;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Museums;
import olmerk.rococo.jupiter.annotation.Token;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.RestTest;
import olmerk.rococo.jupiter.extension.ApiLoginExtension;
import olmerk.rococo.model.*;
import olmerk.rococo.service.MuseumClient;
import olmerk.rococo.service.impl.GateApiClient;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

import java.io.IOException;
import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;


@RestTest
public class MuseumRestTest {
    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();
    private final GateApiClient gateApiClient = new GateApiClient();
    private final MuseumClient museumClient = MuseumClient.getInstance();
    private final CountryJson country = museumClient.getCountry("Россия");
    private final ObjectMapper mapper = new ObjectMapper();


    @Test
    @Museums(countOfMuseums = 3)
    void searchMuseumTest(MuseumJson[] museumJsons){
        Page<MuseumJson> museumJsonListSearched = gateApiClient.searchMuseum(museumJsons[0].title());
        step("Check searched museum", () ->
                Assertions.assertAll(
                        () -> assertEquals(1, museumJsonListSearched.getTotalElements()),
                        () -> assertEquals(museumJsons[0].title(), museumJsonListSearched.getContent().get(0).title()),
                        () -> assertEquals(museumJsons[0].description(), museumJsonListSearched.getContent().get(0).description())
                )
        );
    }

    @Test
    @Museums(countOfMuseums = 3)
    void paginationMuseumsTest(){
        Page<MuseumJson> museumJsonListSearched = gateApiClient.paginationMuseums(2, 0);
        step("Check searched museums", () ->
                Assertions.assertAll(
                        () -> assertEquals(2, museumJsonListSearched.getTotalElements())
                )
        );
    }

    @Test
    void cantCreateMuseumNotRegisteredUserTest(){
        gateApiClient.createMuseumWithError("Bearer " + RandomDataUtils.artistName(), new MuseumJson(null, RandomDataUtils.randomName(),
                RandomDataUtils.randomName(), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, RandomDataUtils.randomCity())),
                HttpStatus.UNAUTHORIZED_401);
    }

    @Test
    @User
    @ApiLogin
    void cantCreateMuseumWithOutTitleTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createMuseumWithError("Bearer " + bearerToken, new MuseumJson(null, "",
                        RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, RandomDataUtils.randomCity())),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(errors.errors().stream().sorted().toList(), List.of("Title can not be blank", "Allowed title length should be from 3 to 225 characters").stream().sorted().toList())
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateMuseumWithShorttitleTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createMuseumWithError("Bearer " + bearerToken, new MuseumJson(null, "QW",
                        RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, RandomDataUtils.randomCity())),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(errors.errors().stream().sorted().toList(), List.of("Allowed title length should be from 3 to 225 characters").stream().sorted().toList())
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateMuseumWithOutDescriptionTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createMuseumWithError("Bearer " + bearerToken, new MuseumJson(null, "QWASDF",
                        "", PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, RandomDataUtils.randomCity())),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Allowed description length should be from 10 to 1000 characters", "description can not be blank").stream().sorted().toList(),
                        errors.errors().stream().sorted().toList()));
    }

    @Test
    @User
    @ApiLogin
    void cantCreateMuseumWithShortDescriptionTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createMuseumWithError("Bearer " + bearerToken, new MuseumJson(null, "QWASDF",
                        "ASDFG", PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, RandomDataUtils.randomCity())),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Allowed description length should be from 10 to 1000 characters"), errors.errors())
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateMuseumWithBigPhotoTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createMuseumWithError("Bearer " + bearerToken, new MuseumJson(null, "QWASDF",
                        RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/bigphoto.jpg"), new GeoJson(country, RandomDataUtils.randomCity())),
                HttpStatus.BAD_REQUEST_400);
        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Photo is should be 1Mb"), errors.errors())
        );
    }
}
