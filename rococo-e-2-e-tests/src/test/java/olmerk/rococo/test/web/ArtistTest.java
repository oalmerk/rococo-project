package olmerk.rococo.test.web;

import org.junit.jupiter.api.Test;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Artist;
import olmerk.rococo.jupiter.annotation.Museum;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.WebTest;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.page.artist.ArtistCardPage;
import olmerk.rococo.page.common.MainPage;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

@WebTest
public class ArtistTest {

    private final String biographyKan = "Василий Васильевич Кандинский – русский живописец, график и теоретик изобразительного искусства," +
            " один из основоположников абстракционизма.";

    @Test
    @User
    @ApiLogin
    void successCreationArtistTest() {
        String randomName = RandomDataUtils.randomName();
        String randomBiography = RandomDataUtils.randomSentence(10);
        new MainPage()
                .getHeader()
                .clickArtistButton()
                .checkOpen()
                .clickAddArtistButton()
                .checkOpen()
                .setName(randomName)
                .setPhoto("pepin.jpg")
                .setBiography(randomBiography)
                .clickSubmitButton()
                .clickArtists(randomName);
    }

    @Test
    @User
    @ApiLogin
    @Artist(name = "Кандинский Василий Васильевич", biography = biographyKan, photo = "images/kandinskiy.jpg")
    void successUpdatingArtistTest(ArtistJson artistJson) {
        new MainPage()
                .getHeader()
                .clickArtistButton()
                .checkOpen()
                .clickArtists(artistJson.name())
                .checkOpen()
                .checkArtist(artistJson)
                .clickEdit()
                .checkBiography(artistJson.biography())
                .checkName(artistJson.name())
                .setPhoto("kandinskiy2.jpg")
                .clickSubmitButton()
                .checkPhoto(PhotoUtils.fromFile("images/kandinskiy2.jpg"));
    }

    @Test
    @User
    @ApiLogin
    @Artist(name = "Кандинский Василий", biography = "Русский живописец, график и теоретик изобразительного искусства", photo = "images/kandinskiy.jpg", deleteAfterTest = true)
    @Museum(title = "Музей Соломона Гуггенхайма1", description = "Mузей искусства в США", photo = "images/uggenheimuseum.jpg",
            city = "Нью-Йорк", countryName = "Соединённые Штаты Америки")
    void addPaintingToArtistTest(ArtistJson artistJson, MuseumJson museumJson) {
        String randomName = RandomDataUtils.randomSentence(3);
        String randomDescription = RandomDataUtils.randomSentence(10);
        new MainPage()
                .getHeader()
                .clickArtistButton()
                .checkOpen()
                .clickArtists(artistJson.name())
                .checkOpen()
                .clickAddPainting()
                .setName(randomName)
                .setDescription(randomDescription)
                .setPhoto("сomposition8.jpg")
                .selectMuseum(museumJson.title())
                .clickSubmitButton(new ArtistCardPage())
                .checkPainting(randomName);
    }
}
