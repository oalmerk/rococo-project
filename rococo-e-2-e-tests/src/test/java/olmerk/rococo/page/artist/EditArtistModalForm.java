package olmerk.rococo.page.artist;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import olmerk.rococo.page.BasePage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class EditArtistModalForm extends BasePage<EditArtistModalForm> {

    private final String BASE = ".w-modal";
    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement photoInput = $("input[name='photo']");
    private final SelenideElement biographyInput = $("textarea[name='biography']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement closeButton = $("button[type='button']");

    @Override
    public EditArtistModalForm checkOpen() {
        nameInput.shouldBe(visible);
        photoInput.shouldBe(visible);
        biographyInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        closeButton.shouldBe(visible);
        $(BASE + " header").shouldBe(exactText("Редактировать художника"));
        return this;
    }

    @Nonnull
    public EditArtistModalForm setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    public EditArtistModalForm setPhoto(String photoName) {
        photoInput.uploadFromClasspath("images/" + photoName);
        return this;
    }

    @Nonnull
    public EditArtistModalForm checkName(String name) {
        Assertions.assertEquals(nameInput.getValue(), name);
        return this;
    }

    @Nonnull
    public EditArtistModalForm checkBiography(String biography) {
        Assertions.assertEquals(biographyInput.getValue(), biography);
        return this;
    }

    @Nonnull
    public EditArtistModalForm setBiography(String biography) {
        biographyInput.setValue(biography);
        return this;
    }

    @Nonnull
    public ArtistCardPage clickSubmitButton() {
        submitButton.shouldBe(enabled).click();
        return new ArtistCardPage();
    }
}
