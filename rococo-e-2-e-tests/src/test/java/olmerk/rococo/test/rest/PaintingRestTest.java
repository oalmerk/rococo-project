package olmerk.rococo.test.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.Page;
import retrofit2.Response;
import olmerk.rococo.jupiter.annotation.*;
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
public class PaintingRestTest {
    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();
    private final GateApiClient gateApiClient = new GateApiClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final MuseumClient museumClient = MuseumClient.getInstance();
    CountryJson country = museumClient.getCountry("Россия");
    private final ArtistJson artistJson = new ArtistJson(null, RandomDataUtils.artistName(),
                                RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/kandinskiy2.jpg"));
    private final MuseumJson museumJson = new MuseumJson(null, "Русский музей",
            RandomDataUtils.randomSentence(10), PhotoUtils.fromFile("images/russmuseum.jpg"), new GeoJson(country, "Санкт-Петербург"));


    @Test
    @Paintings(countOfPaintings = 3)
    void searchPaintingTest(PaintingJson[] paintingJsons){
        Page<PaintingJson> paintingJsonPage = gateApiClient.searchPainting(paintingJsons[0].title());
        step("Check searched museum", () ->
                Assertions.assertAll(
                        () -> assertEquals(1, paintingJsonPage.getTotalElements()),
                        () -> assertEquals(paintingJsons[0].title(), paintingJsonPage.getContent().get(0).title()),
                        () -> assertEquals(paintingJsons[0].description(), paintingJsonPage.getContent().get(0).description())
                )
        );
    }

    @Test
    @Paintings(countOfPaintings = 3)
    void paginationPaintingTest(){
        Page<PaintingJson> paintingJsonPage = gateApiClient.paginationPainting(2, 0);
        step("Check searched museums", () ->
                Assertions.assertAll(
                        () -> assertEquals(2, paintingJsonPage.getTotalElements())
                )
        );
    }

    @Test
    void cantCreatePaintingNotRegisteredUserTest(){
        gateApiClient.createPaintingWithError("Bearer " + RandomDataUtils.artistName(), new PaintingJson(null,  RandomDataUtils.artistName(),
                RandomDataUtils.randomSentence(5), PhotoUtils.fromFile("images/сomposition8.jpg"), artistJson.id(), museumJson.id()),
                HttpStatus.UNAUTHORIZED_401);
    }

    @Test
    @User
    @ApiLogin
    void cantCreatePaintingWithOutTitleTest(@Token String bearerToken) throws IOException {
        Response response =   gateApiClient.createPaintingWithError("Bearer " + bearerToken, new PaintingJson(null, "",
                        RandomDataUtils.randomSentence(5), PhotoUtils.fromFile("images/сomposition8.jpg"), artistJson.id(), museumJson.id()),
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
    void cantCreatePaintingWithShortTitleTest(@Token String bearerToken) throws IOException {
        Response response =   gateApiClient.createPaintingWithError("Bearer " + bearerToken, new PaintingJson(null, "QW",
                        RandomDataUtils.randomSentence(5), PhotoUtils.fromFile("images/сomposition8.jpg"), artistJson.id(), museumJson.id()),
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
    void cantCreatePaintingWithOutDescriptionTest(@Token String bearerToken) throws IOException {
        Response response =   gateApiClient.createPaintingWithError("Bearer " + bearerToken, new PaintingJson(null, "QWASD",
                       "", PhotoUtils.fromFile("images/сomposition8.jpg"), artistJson.id(), museumJson.id()),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Allowed description length should be from 10 to 225 characters", "description can not be blank").stream().sorted().toList(),
                        errors.errors().stream().sorted().toList()));
    }

    @Test
    @User
    @ApiLogin
    void cantCreatePaintingWithShortDescriptionTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createPaintingWithError("Bearer " + bearerToken, new PaintingJson(null, "QWASD",
                        "QWASD", PhotoUtils.fromFile("images/сomposition8.jpg"), artistJson.id(), museumJson.id()),
                HttpStatus.BAD_REQUEST_400);

        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Allowed description length should be from 10 to 225 characters"), errors.errors())
        );
    }

    @Test
    @User
    @ApiLogin
    void cantCreateMuseumWithBigPhotoTest(@Token String bearerToken) throws IOException {
        Response response = gateApiClient.createPaintingWithError("Bearer " + bearerToken, new PaintingJson(null, "QWASD",
                        RandomDataUtils.randomSentence(10),PhotoUtils.fromFile("images/bigphoto.jpg"), artistJson.id(), museumJson.id()),
                HttpStatus.BAD_REQUEST_400);
        String errorJson = response.errorBody().string();
        ErrorsJson errors = mapper.readValue(errorJson, ErrorsJson.class);

        step("Check error response",
                () -> assertEquals(List.of("Photo is should be 1Mb"), errors.errors())
        );
    }
}
