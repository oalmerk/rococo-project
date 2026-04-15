package olmerk.rococo.page.museum;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

public class CreateUpdateMuseumForm extends BasePage<CreateUpdateMuseumForm> {

    private final SelenideElement nameInput = $("input[name='title']");
    private final SelenideElement photoInput = $("input[name='photo']");
    private final SelenideElement cityInput = $("input[name='city']");
    private final SelenideElement descriptionInput = $("textarea[name='description']");
    private final SelenideElement countrySelect = $("select[name='countryId']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement closeButton = $("button[type='button']");
    private final SelenideElement imageMuseum = $("img");

    @Override
    public CreateUpdateMuseumForm checkOpen() {
        nameInput.shouldBe(visible);
        photoInput.shouldBe(visible);
        descriptionInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        closeButton.shouldBe(visible);
        return this;
    }

    @Nonnull
    public CreateUpdateMuseumForm setTitle(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    public CreateUpdateMuseumForm setCity(String city) {
        cityInput.setValue(city);
        return this;
    }

    @Nonnull
    public CreateUpdateMuseumForm setPhoto(String photoName) {
        photoInput.uploadFromClasspath("images/" + photoName);
        return this;
    }

    @Nonnull
    public CreateUpdateMuseumForm setDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    public CreateUpdateMuseumForm selectCountry(String country) {
        countrySelect.selectOption(country);
        return this;
    }

    public CreateUpdateMuseumForm checkMuseumImage( String photo) {
        imageMuseum.shouldBe(visible, attribute("src", photo));
        return this;
    }

    @Nonnull
    public <T extends BasePage<?>> T clickSubmitButton(T returnPage) {
        submitButton.shouldBe(enabled).scrollIntoView(true).click();
        return returnPage;
    }
}
