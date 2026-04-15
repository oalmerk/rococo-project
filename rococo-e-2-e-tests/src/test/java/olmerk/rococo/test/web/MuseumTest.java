package olmerk.rococo.test.web;

import org.junit.jupiter.api.Test;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Museum;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.WebTest;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.page.common.MainPage;
import olmerk.rococo.page.museum.MuseumCardPage;
import olmerk.rococo.page.museum.MuseumPage;
import olmerk.rococo.utils.PhotoUtils;
import olmerk.rococo.utils.RandomDataUtils;

@WebTest
public class MuseumTest {

    private final String title = RandomDataUtils.randomName();
    private final String description = RandomDataUtils.randomSentence(6);

    @Test
    @User
    @ApiLogin
    void successCreationMuseumTest() {
        new MainPage()
                .getHeader()
                .clickMuseumButton()
                .checkOpen()
                .clickAddMuseumButton()
                .checkOpen()
                .setTitle(title)
                .setDescription(description)
                .setCity("Санкт-Петербург")
                .selectCountry("Россия")
                .setPhoto("russmuseum.jpg")
                .clickSubmitButton(new MuseumPage())
                .clickMuseum(title + "\n" +
                        "Санкт-Петербург, Россия")
                .checkMuseum(title, description, "Россия, Санкт-Петербург", PhotoUtils.fromFile("images/russmuseum.jpg"));
    }


    @Test
    @User
    @ApiLogin
    @Museum(title = "Музей Соломона Гуггенхайма", description = "Mузей искусства в США", photo = "images/uggenheimuseum.jpg",
            city = "Нью-Йорк", countryName = "Соединённые Штаты Америки")
    void successUpdateMuseumTest(MuseumJson museumJson) {
        String city = "Соединённые Штаты Америки, Нью-Йорк";
        new MainPage()
                .getHeader()
                .clickMuseumButton()
                .checkOpen()
                .clickMuseum(museumJson.title() + "\n" +
                        "Нью-Йорк, Соединённые Штаты Америки")
                .checkMuseum(museumJson.title(), museumJson.description(), city, museumJson.photo())
                .clickUpdateButton()
                .setPhoto("uggenheimuseum2.jpg")
                .clickSubmitButton(new MuseumCardPage())
                .checkMuseum(museumJson.title(), museumJson.description(), city, PhotoUtils.fromFile("images/uggenheimuseum2.jpg"));
    }
}
