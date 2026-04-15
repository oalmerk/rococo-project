package olmerk.rococo.page.artist;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class CreateArtistModalForm extends BasePage<CreateArtistModalForm> {

    private final String BASE = ".w-modal";
    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement photoInput = $("input[name='photo']");
    private final SelenideElement biographyInput = $("textarea[name='biography']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement closeButton = $("button[type='button']");

    @Override
    public CreateArtistModalForm checkOpen() {
        nameInput.shouldBe(visible);
        photoInput.shouldBe(visible);
        biographyInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        closeButton.shouldBe(visible);
        $(BASE + " header").shouldBe(exactText("Новый художник"));
        $(BASE + " article").shouldBe(exactText("Заполните форму, чтобы добавить нового художника"));
        return this;
    }

    @Nonnull
    public CreateArtistModalForm setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    public CreateArtistModalForm setPhoto(String photoName) {
        photoInput.uploadFromClasspath("images/" + photoName);
        return this;
    }

    @Nonnull
    public CreateArtistModalForm setBiography(String biography) {
        biographyInput.setValue(biography);
        return this;
    }

    @Nonnull
    public ArtistsPage clickSubmitButton() {
        submitButton.shouldBe(enabled).click();
        return new ArtistsPage();
    }
}
