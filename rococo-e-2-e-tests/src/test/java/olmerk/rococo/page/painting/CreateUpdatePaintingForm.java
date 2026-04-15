package olmerk.rococo.page.painting;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class CreateUpdatePaintingForm extends BasePage<CreateUpdatePaintingForm>{

    private final SelenideElement nameInput = $("input[name='title']");
    private final SelenideElement photoInput = $("input[name='content']");
    private final SelenideElement descriptionInput = $("textarea[name='description']");
    private final SelenideElement museumSelect = $("select[name='museumId']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement closeButton = $("button[type='button']");

    @Override
    public CreateUpdatePaintingForm checkOpen() {
        nameInput.shouldBe(visible);
        photoInput.shouldBe(visible);
        descriptionInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        closeButton.shouldBe(visible);
        return this;
    }

    @Nonnull
    public CreateUpdatePaintingForm setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    public CreateUpdatePaintingForm setPhoto(String photoName) {
        photoInput.uploadFromClasspath("images/" + photoName);
        return this;
    }

    @Nonnull
    public CreateUpdatePaintingForm setDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    public CreateUpdatePaintingForm selectMuseum(String museum) {
        museumSelect.selectOption(museum);
        return this;
    }

    @Nonnull
    public <T extends BasePage<?>> T clickSubmitButton(T returPage) {
        submitButton.scrollIntoView(true).shouldBe(enabled).click();
        return returPage;
    }
}
